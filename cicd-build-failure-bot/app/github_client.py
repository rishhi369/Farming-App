from __future__ import annotations

import hashlib
import hmac
import json
from typing import Any
from urllib import error, request


GITHUB_API = "https://api.github.com"


def verify_github_signature(secret: str, body: bytes, signature: str) -> bool:
    if not secret:
        return True
    if not signature or not signature.startswith("sha256="):
        return False
    expected = "sha256=" + hmac.new(secret.encode("utf-8"), body, hashlib.sha256).hexdigest()
    return hmac.compare_digest(expected, signature)


class GitHubClient:
    def __init__(self, token: str) -> None:
        self.token = token

    def create_commit_status(
        self,
        repo_full_name: str,
        sha: str,
        state: str,
        description: str,
        context: str,
        target_url: str | None = None,
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {
            "state": state,
            "description": description[:140],
            "context": context,
        }
        if target_url:
            payload["target_url"] = target_url
        return self._request("POST", f"/repos/{repo_full_name}/statuses/{sha}", payload)

    def create_pr_comment(self, repo_full_name: str, issue_number: int, body: str) -> dict[str, Any]:
        return self._request("POST", f"/repos/{repo_full_name}/issues/{issue_number}/comments", {"body": body})

    def _request(self, method: str, path: str, payload: dict[str, Any]) -> dict[str, Any]:
        if not self.token:
            raise ValueError("GITHUB_TOKEN is required for GitHub write operations")

        data = json.dumps(payload).encode("utf-8")
        req = request.Request(
            GITHUB_API + path,
            data=data,
            method=method,
            headers={
                "Accept": "application/vnd.github+json",
                "Authorization": f"Bearer {self.token}",
                "Content-Type": "application/json",
                "User-Agent": "cicd-build-failure-bot",
                "X-GitHub-Api-Version": "2022-11-28",
            },
        )

        try:
            with request.urlopen(req, timeout=10) as response:
                return json.loads(response.read().decode("utf-8") or "{}")
        except error.HTTPError as exc:
            details = exc.read().decode("utf-8", errors="replace")
            raise RuntimeError(f"GitHub API {exc.code}: {details}") from exc


def build_prediction_comment(prediction: dict[str, Any]) -> str:
    probability = prediction["failure_probability"]
    risk = prediction["risk_level"].upper()
    signals = prediction.get("top_signals", [])
    lines = [
        f"CI failure risk: **{risk}** ({probability:.1%}).",
        "",
        "Top signals:",
    ]
    for signal in signals[:5]:
        lines.append(f"- `{signal['feature']}` = `{signal['value']}` ({signal['direction']})")
    return "\n".join(lines)
