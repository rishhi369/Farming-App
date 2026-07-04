from dataclasses import dataclass
from pathlib import Path
import os


BASE_DIR = Path(__file__).resolve().parents[1]


def _bool_env(name: str, default: bool) -> bool:
    raw = os.getenv(name)
    if raw is None:
        return default
    return raw.strip().lower() in {"1", "true", "yes", "y", "on"}


@dataclass(frozen=True)
class Settings:
    model_path: Path
    github_webhook_secret: str
    github_token: str
    github_status_context: str
    bot_dry_run: bool


def get_settings() -> Settings:
    model_path = Path(os.getenv("MODEL_PATH", BASE_DIR / "artifacts" / "model.joblib"))
    if not model_path.is_absolute():
        model_path = BASE_DIR / model_path

    return Settings(
        model_path=model_path,
        github_webhook_secret=os.getenv("GITHUB_WEBHOOK_SECRET", ""),
        github_token=os.getenv("GITHUB_TOKEN", ""),
        github_status_context=os.getenv("GITHUB_STATUS_CONTEXT", "ci-failure-predictor"),
        bot_dry_run=_bool_env("BOT_DRY_RUN", True),
    )
