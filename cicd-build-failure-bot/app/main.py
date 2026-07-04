from __future__ import annotations

from typing import Any

from flask import Flask, abort, jsonify, request

from app.config import Settings, get_settings
from app.feature_engineering import github_event_to_features, normalize_features
from app.github_client import GitHubClient, build_prediction_comment, verify_github_signature
from app.model_service import ModelService


def create_app(settings: Settings | None = None) -> Flask:
    app = Flask(__name__)
    active_settings = settings or get_settings()
    model_service = ModelService(active_settings.model_path)

    @app.get("/health")
    def health() -> Any:
        return jsonify(
            {
                "status": "ok",
                "model_loaded": model_service.loaded,
                "model_path": str(active_settings.model_path),
            }
        )

    @app.get("/model")
    def model_metadata() -> Any:
        return jsonify(model_service.metadata)

    @app.post("/predict")
    def predict() -> Any:
        payload = request.get_json(silent=True) or {}
        raw_features = payload.get("features", payload)
        prediction = model_service.predict(normalize_features(raw_features))
        return jsonify(prediction.as_dict())

    @app.post("/github/webhook")
    def github_webhook() -> Any:
        body = request.get_data()
        signature = request.headers.get("X-Hub-Signature-256", "")
        if active_settings.github_webhook_secret and not verify_github_signature(
            active_settings.github_webhook_secret, body, signature
        ):
            abort(401, "Invalid GitHub signature")

        payload = request.get_json(silent=True) or {}
        event_name = request.headers.get("X-GitHub-Event", "")
        features = github_event_to_features(event_name, payload)
        if features is None:
            return jsonify({"ignored": True, "event": event_name, "reason": "unsupported_event"}), 202

        prediction = model_service.predict(features).as_dict()
        github_result = _maybe_notify_github(active_settings, payload, prediction)

        return jsonify(
            {
                "ignored": False,
                "event": event_name,
                "delivery": request.headers.get("X-GitHub-Delivery"),
                "prediction": prediction,
                "github": github_result,
            }
        )

    return app


def _maybe_notify_github(settings: Settings, payload: dict[str, Any], prediction: dict[str, Any]) -> dict[str, Any]:
    repo_full_name, sha, pr_number = _github_targets(payload)
    if settings.bot_dry_run or not settings.github_token:
        return {
            "mode": "dry_run",
            "repo": repo_full_name,
            "sha": sha,
            "would_warn": prediction["should_warn"],
        }
    if not repo_full_name or not sha:
        return {"mode": "skipped", "reason": "missing_repo_or_sha"}

    client = GitHubClient(settings.github_token)
    state = "failure" if prediction["should_warn"] else "success"
    description = f"{prediction['risk_level']} failure risk ({prediction['failure_probability']:.0%})"
    status = client.create_commit_status(
        repo_full_name=repo_full_name,
        sha=sha,
        state=state,
        description=description,
        context=settings.github_status_context,
    )

    comment = None
    if prediction["should_warn"] and pr_number:
        comment = client.create_pr_comment(repo_full_name, int(pr_number), build_prediction_comment(prediction))

    return {"mode": "written", "status_id": status.get("id"), "comment_id": (comment or {}).get("id")}


def _github_targets(payload: dict[str, Any]) -> tuple[str | None, str | None, int | None]:
    repo = (payload.get("repository") or {}).get("full_name")
    pr = payload.get("pull_request") or {}
    run = payload.get("workflow_run") or {}
    suite = payload.get("check_suite") or {}
    job = payload.get("workflow_job") or {}

    sha = (
        pr.get("head", {}).get("sha")
        or run.get("head_sha")
        or suite.get("head_sha")
        or job.get("head_sha")
        or payload.get("after")
    )

    pr_number = pr.get("number")
    pull_requests = run.get("pull_requests") or suite.get("pull_requests") or []
    if not pr_number and pull_requests:
        pr_number = pull_requests[0].get("number")

    return repo, sha, pr_number


app = create_app()
