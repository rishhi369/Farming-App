from __future__ import annotations

import argparse
from pathlib import Path

import numpy as np
import pandas as pd


def make_sample_data(rows: int = 3000, seed: int = 42) -> pd.DataFrame:
    rng = np.random.default_rng(seed)

    branch = rng.choice(
        ["main", "develop", "feature", "bugfix", "hotfix", "release", "dependabot"],
        size=rows,
        p=[0.12, 0.14, 0.36, 0.16, 0.06, 0.08, 0.08],
    )
    runner_os = rng.choice(["ubuntu-latest", "windows-latest", "macos-latest"], size=rows, p=[0.78, 0.15, 0.07])
    repo_language = rng.choice(["Python", "JavaScript", "Java", "Kotlin", "Go"], size=rows)
    event_name = rng.choice(["pull_request", "push", "workflow_run"], size=rows, p=[0.68, 0.22, 0.10])

    changed_files = rng.negative_binomial(3, 0.22, size=rows) + 1
    additions = rng.gamma(shape=2.2, scale=95, size=rows).astype(int)
    deletions = rng.gamma(shape=1.8, scale=55, size=rows).astype(int)
    commits = np.maximum(1, rng.poisson(2.4, size=rows))
    previous_failed_runs_7d = rng.poisson(0.7, size=rows)
    flaky_tests_7d = rng.poisson(0.9, size=rows)
    test_count = np.maximum(25, rng.normal(420, 160, size=rows).astype(int))
    lint_warnings = rng.poisson(4, size=rows)
    dependency_files_changed = rng.binomial(1, 0.18, size=rows)
    touched_services = np.maximum(1, np.minimum(14, (changed_files / rng.integers(5, 12, size=rows)).astype(int) + 1))
    parallel_jobs = rng.integers(1, 12, size=rows)
    cache_hit_rate = np.clip(rng.beta(7, 3, size=rows), 0.05, 0.99)
    rerun_count = rng.poisson(0.18, size=rows)
    workflow_age_days = rng.integers(10, 1200, size=rows)
    is_weekend = rng.binomial(1, 0.22, size=rows)

    failed_tests_signal = rng.poisson(
        np.clip(0.04 * previous_failed_runs_7d + 0.05 * flaky_tests_7d + 0.02 * dependency_files_changed, 0.02, 2.5)
    )

    logit = -3.15
    logit += 0.018 * changed_files
    logit += 0.0011 * additions
    logit += 0.0014 * deletions
    logit += 0.20 * commits
    logit += 0.52 * previous_failed_runs_7d
    logit += 0.42 * flaky_tests_7d
    logit += 0.88 * failed_tests_signal
    logit += 0.05 * lint_warnings
    logit += 0.74 * dependency_files_changed
    logit += 0.15 * touched_services
    logit += 0.24 * rerun_count
    logit += 0.35 * is_weekend
    logit -= 1.35 * cache_hit_rate
    logit += np.where(branch == "hotfix", 0.45, 0)
    logit += np.where(branch == "release", 0.28, 0)
    logit += np.where(runner_os == "windows-latest", 0.24, 0)

    probability = 1 / (1 + np.exp(-logit))
    build_failed = rng.binomial(1, probability)

    return pd.DataFrame(
        {
            "changed_files": changed_files,
            "additions": additions,
            "deletions": deletions,
            "commits": commits,
            "duration_seconds": rng.gamma(3.2, 95, size=rows).astype(int),
            "queued_seconds": rng.gamma(1.6, 26, size=rows).astype(int),
            "previous_failed_runs_7d": previous_failed_runs_7d,
            "flaky_tests_7d": flaky_tests_7d,
            "test_count": test_count,
            "failed_tests": failed_tests_signal,
            "lint_warnings": lint_warnings,
            "dependency_files_changed": dependency_files_changed,
            "touched_services": touched_services,
            "parallel_jobs": parallel_jobs,
            "cache_hit_rate": cache_hit_rate.round(3),
            "rerun_count": rerun_count,
            "workflow_age_days": workflow_age_days,
            "is_weekend": is_weekend,
            "event_name": event_name,
            "branch": branch,
            "runner_os": runner_os,
            "repo_language": repo_language,
            "actor_association": rng.choice(["MEMBER", "CONTRIBUTOR", "FIRST_TIME_CONTRIBUTOR"], size=rows),
            "workflow_name": rng.choice(["ci", "test", "build", "release", "lint-and-test"], size=rows),
            "label_hotfix": np.where(branch == "hotfix", "true", "false"),
            "label_release": np.where(branch == "release", "true", "false"),
            "build_failed": build_failed,
        }
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Generate demo CI build history for model training.")
    parser.add_argument("--rows", type=int, default=3000)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--output", default="data/sample_builds.csv")
    args = parser.parse_args()

    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    frame = make_sample_data(rows=args.rows, seed=args.seed)
    frame.to_csv(output, index=False)
    print(f"Wrote {len(frame)} rows to {output} with failure rate {frame['build_failed'].mean():.2%}")


if __name__ == "__main__":
    main()
