from __future__ import annotations

from datetime import datetime, timezone
from typing import Any


NUMERIC_FEATURES = [
    "changed_files",
    "additions",
    "deletions",
    "commits",
    "duration_seconds",
    "queued_seconds",
    "previous_failed_runs_7d",
    "flaky_tests_7d",
    "test_count",
    "failed_tests",
    "lint_warnings",
    "dependency_files_changed",
    "touched_services",
    "parallel_jobs",
    "cache_hit_rate",
    "rerun_count",
    "workflow_age_days",
    "is_weekend",
]

CATEGORICAL_FEATURES = [
    "event_name",
    "branch",
    "runner_os",
    "repo_language",
    "actor_association",
    "workflow_name",
    "label_hotfix",
    "label_release",
]

ALL_FEATURES = NUMERIC_FEATURES + CATEGORICAL_FEATURES

DEFAULT_FEATURES: dict[str, Any] = {
    "changed_files": 0,
    "additions": 0,
    "deletions": 0,
    "commits": 1,
    "duration_seconds": 0,
    "queued_seconds": 0,
    "previous_failed_runs_7d": 0,
    "flaky_tests_7d": 0,
    "test_count": 0,
    "failed_tests": 0,
    "lint_warnings": 0,
    "dependency_files_changed": 0,
    "touched_services": 1,
    "parallel_jobs": 1,
    "cache_hit_rate": 0.75,
    "rerun_count": 0,
    "workflow_age_days": 365,
    "is_weekend": 0,
    "event_name": "workflow_run",
    "branch": "main",
    "runner_os": "ubuntu-latest",
    "repo_language": "Python",
    "actor_association": "CONTRIBUTOR",
    "workflow_name": "ci",
    "label_hotfix": "false",
    "label_release": "false",
}

DEPENDENCY_FILE_HINTS = (
    "requirements",
    "poetry.lock",
    "pyproject.toml",
    "package-lock.json",
    "pnpm-lock.yaml",
    "yarn.lock",
    "pom.xml",
    "build.gradle",
    "gradle.lockfile",
    "dockerfile",
)


def normalize_features(raw: dict[str, Any] | None) -> dict[str, Any]:
    values = {**DEFAULT_FEATURES, **(raw or {})}
    normalized: dict[str, Any] = {}

    for feature in NUMERIC_FEATURES:
        normalized[feature] = _to_number(values.get(feature), DEFAULT_FEATURES[feature])

    normalized["dependency_files_changed"] = min(1, int(normalized["dependency_files_changed"]))
    normalized["is_weekend"] = min(1, int(normalized["is_weekend"]))
    normalized["cache_hit_rate"] = max(0.0, min(1.0, float(normalized["cache_hit_rate"])))

    for feature in CATEGORICAL_FEATURES:
        normalized[feature] = str(values.get(feature, DEFAULT_FEATURES[feature]) or DEFAULT_FEATURES[feature])

    return normalized


def github_event_to_features(event_name: str, payload: dict[str, Any]) -> dict[str, Any] | None:
    event_name = (event_name or "").strip()
    if event_name == "pull_request":
        return _pull_request_features(payload)
    if event_name == "workflow_run":
        return _workflow_run_features(payload)
    if event_name == "workflow_job":
        return _workflow_job_features(payload)
    if event_name == "check_suite":
        return _check_suite_features(payload)
    return None


def _pull_request_features(payload: dict[str, Any]) -> dict[str, Any]:
    pr = payload.get("pull_request", {}) or {}
    repo = payload.get("repository", {}) or {}
    labels = {str(label.get("name", "")).lower() for label in pr.get("labels", [])}
    files = _filename_list(payload)

    return normalize_features(
        {
            "event_name": "pull_request",
            "branch": _branch_family(pr.get("head", {}).get("ref") or "unknown"),
            "repo_language": repo.get("language") or "Unknown",
            "actor_association": pr.get("author_association") or "CONTRIBUTOR",
            "workflow_name": "pre-merge",
            "changed_files": pr.get("changed_files", 0),
            "additions": pr.get("additions", 0),
            "deletions": pr.get("deletions", 0),
            "commits": pr.get("commits", 1),
            "dependency_files_changed": _has_dependency_change(files),
            "touched_services": _estimate_touched_services(files, pr.get("changed_files", 0)),
            "label_hotfix": str(any("hotfix" in label for label in labels)).lower(),
            "label_release": str(any("release" in label for label in labels)).lower(),
            "is_weekend": _is_weekend(pr.get("created_at")),
        }
    )


def _workflow_run_features(payload: dict[str, Any]) -> dict[str, Any]:
    run = payload.get("workflow_run", {}) or {}
    repo = payload.get("repository", {}) or {}
    duration = _seconds_between(run.get("run_started_at"), run.get("updated_at"))
    queued = _seconds_between(run.get("created_at"), run.get("run_started_at"))
    conclusion = run.get("conclusion")

    return normalize_features(
        {
            "event_name": run.get("event") or "workflow_run",
            "branch": _branch_family(run.get("head_branch") or "unknown"),
            "repo_language": repo.get("language") or "Unknown",
            "actor_association": "CONTRIBUTOR",
            "workflow_name": run.get("name") or "ci",
            "duration_seconds": duration,
            "queued_seconds": queued,
            "failed_tests": 1 if conclusion == "failure" else 0,
            "runner_os": "ubuntu-latest",
            "rerun_count": max(0, int(run.get("run_attempt", 1) or 1) - 1),
            "is_weekend": _is_weekend(run.get("created_at")),
        }
    )


def _workflow_job_features(payload: dict[str, Any]) -> dict[str, Any]:
    job = payload.get("workflow_job", {}) or {}
    repo = payload.get("repository", {}) or {}
    labels = [str(label).lower() for label in job.get("labels", [])]
    duration = _seconds_between(job.get("started_at"), job.get("completed_at"))
    conclusion = job.get("conclusion")

    return normalize_features(
        {
            "event_name": "workflow_job",
            "branch": "unknown",
            "repo_language": repo.get("language") or "Unknown",
            "workflow_name": job.get("name") or "ci",
            "duration_seconds": duration,
            "failed_tests": 1 if conclusion == "failure" else 0,
            "parallel_jobs": len(job.get("steps", []) or []) or 1,
            "runner_os": _runner_from_labels(labels),
            "is_weekend": _is_weekend(job.get("started_at")),
        }
    )


def _check_suite_features(payload: dict[str, Any]) -> dict[str, Any]:
    suite = payload.get("check_suite", {}) or {}
    repo = payload.get("repository", {}) or {}
    conclusion = suite.get("conclusion")

    return normalize_features(
        {
            "event_name": "check_suite",
            "branch": _branch_family(suite.get("head_branch") or "unknown"),
            "repo_language": repo.get("language") or "Unknown",
            "workflow_name": "checks",
            "failed_tests": 1 if conclusion == "failure" else 0,
            "is_weekend": _is_weekend(suite.get("created_at")),
        }
    )


def _filename_list(payload: dict[str, Any]) -> list[str]:
    files = payload.get("files") or payload.get("changed_files_detail") or []
    return [str(item.get("filename", item)) for item in files]


def _has_dependency_change(files: list[str]) -> int:
    return int(any(any(hint in name.lower() for hint in DEPENDENCY_FILE_HINTS) for name in files))


def _estimate_touched_services(files: list[str], changed_files: int) -> int:
    if not files:
        return max(1, min(10, int(changed_files or 0) // 8 + 1))
    roots = {name.split("/")[0] for name in files if "/" in name}
    return max(1, min(20, len(roots) or 1))


def _runner_from_labels(labels: list[str]) -> str:
    joined = " ".join(labels)
    if "windows" in joined:
        return "windows-latest"
    if "macos" in joined or "mac" in joined:
        return "macos-latest"
    return "ubuntu-latest"


def _branch_family(branch: str) -> str:
    lowered = str(branch or "unknown").lower()
    for prefix in ("feature/", "bugfix/", "hotfix/", "release/", "dependabot/", "renovate/"):
        if lowered.startswith(prefix):
            return prefix.rstrip("/")
    if lowered in {"main", "master", "develop", "dev"}:
        return lowered
    return "other"


def _to_number(value: Any, default: Any) -> float:
    if isinstance(default, int) and not isinstance(default, bool):
        try:
            return int(float(value))
        except (TypeError, ValueError):
            return int(default)
    try:
        return float(value)
    except (TypeError, ValueError):
        return float(default)


def _parse_time(value: str | None) -> datetime | None:
    if not value:
        return None
    try:
        return datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError:
        return None


def _seconds_between(start: str | None, end: str | None) -> int:
    start_dt = _parse_time(start)
    end_dt = _parse_time(end)
    if not start_dt or not end_dt:
        return 0
    return max(0, int((end_dt - start_dt).total_seconds()))


def _is_weekend(value: str | None) -> int:
    parsed = _parse_time(value) or datetime.now(timezone.utc)
    return int(parsed.weekday() >= 5)
