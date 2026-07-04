from __future__ import annotations

from dataclasses import dataclass
from math import exp, log1p
from pathlib import Path
from typing import Any

from app.feature_engineering import ALL_FEATURES, normalize_features


DEFAULT_THRESHOLD = 0.62


@dataclass
class Prediction:
    failure_probability: float
    risk_level: str
    should_warn: bool
    threshold: float
    model: str
    top_signals: list[dict[str, Any]]

    def as_dict(self) -> dict[str, Any]:
        return {
            "failure_probability": round(self.failure_probability, 4),
            "risk_level": self.risk_level,
            "should_warn": self.should_warn,
            "threshold": self.threshold,
            "model": self.model,
            "top_signals": self.top_signals,
        }


class ModelService:
    def __init__(self, model_path: Path) -> None:
        self.model_path = model_path
        self.bundle: dict[str, Any] | None = None
        self.load_error: str | None = None
        self._load_model()

    @property
    def loaded(self) -> bool:
        return self.bundle is not None

    @property
    def metadata(self) -> dict[str, Any]:
        if not self.bundle:
            return {
                "model": "heuristic_fallback",
                "model_path": str(self.model_path),
                "loaded": False,
                "load_error": self.load_error,
                "features": ALL_FEATURES,
            }
        return {
            **self.bundle.get("metadata", {}),
            "model_path": str(self.model_path),
            "loaded": True,
            "threshold": self.bundle.get("threshold", DEFAULT_THRESHOLD),
            "metrics": self.bundle.get("metrics", {}),
            "features": self.bundle.get("feature_names", ALL_FEATURES),
        }

    def predict(self, raw_features: dict[str, Any]) -> Prediction:
        features = normalize_features(raw_features)
        threshold = DEFAULT_THRESHOLD
        model_name = "heuristic_fallback"

        if self.bundle:
            probability = self._predict_with_bundle(features)
            threshold = float(self.bundle.get("threshold", DEFAULT_THRESHOLD))
            model_name = self.bundle.get("metadata", {}).get("model", "trained_ensemble")
        else:
            probability = _heuristic_probability(features)

        probability = max(0.0, min(1.0, float(probability)))
        risk_level = _risk_level(probability, threshold)
        return Prediction(
            failure_probability=probability,
            risk_level=risk_level,
            should_warn=probability >= threshold,
            threshold=threshold,
            model=model_name,
            top_signals=_explain_heuristic(features),
        )

    def _load_model(self) -> None:
        if not self.model_path.exists():
            return
        try:
            import joblib

            loaded = joblib.load(self.model_path)
            if isinstance(loaded, dict) and "model" in loaded:
                self.bundle = loaded
            else:
                self.bundle = {"model": loaded, "feature_names": ALL_FEATURES}
        except Exception as exc:  # pragma: no cover - defensive path
            self.load_error = str(exc)
            self.bundle = None

    def _predict_with_bundle(self, features: dict[str, Any]) -> float:
        import pandas as pd

        feature_names = self.bundle.get("feature_names", ALL_FEATURES) if self.bundle else ALL_FEATURES
        frame = pd.DataFrame([{name: features.get(name) for name in feature_names}])
        model = self.bundle["model"]
        if hasattr(model, "predict_proba"):
            return float(model.predict_proba(frame)[0][1])
        return float(model.predict(frame)[0])


def _risk_level(probability: float, threshold: float) -> str:
    if probability >= threshold:
        return "high"
    if probability >= max(0.35, threshold - 0.2):
        return "medium"
    return "low"


def _sigmoid(value: float) -> float:
    return 1.0 / (1.0 + exp(-value))


def _heuristic_probability(features: dict[str, Any]) -> float:
    score = -2.35
    score += 0.22 * log1p(float(features["changed_files"]))
    score += 0.18 * log1p(float(features["additions"]) + float(features["deletions"]))
    score += 0.48 * float(features["previous_failed_runs_7d"])
    score += 0.38 * float(features["flaky_tests_7d"])
    score += 0.72 * float(features["failed_tests"])
    score += 0.08 * log1p(float(features["lint_warnings"]))
    score += 0.64 * float(features["dependency_files_changed"])
    score += 0.18 * float(features["touched_services"])
    score += 0.10 * float(features["rerun_count"])
    score += 0.30 * float(features["is_weekend"])
    score -= 1.15 * float(features["cache_hit_rate"])

    if str(features["branch"]).lower() in {"hotfix", "release"}:
        score += 0.42
    if str(features["label_hotfix"]).lower() == "true":
        score += 0.35
    if str(features["runner_os"]).lower().startswith("windows"):
        score += 0.18

    return _sigmoid(score)


def _explain_heuristic(features: dict[str, Any]) -> list[dict[str, Any]]:
    signals = [
        ("previous_failed_runs_7d", float(features["previous_failed_runs_7d"]), "more recent failures increase risk"),
        ("flaky_tests_7d", float(features["flaky_tests_7d"]), "test flakiness increases risk"),
        ("failed_tests", float(features["failed_tests"]), "current failed tests increase risk"),
        ("dependency_files_changed", float(features["dependency_files_changed"]), "dependency changes increase risk"),
        ("changed_files", float(features["changed_files"]), "larger change sets increase risk"),
        ("cache_hit_rate", 1.0 - float(features["cache_hit_rate"]), "low cache hit rate increases risk"),
        ("touched_services", float(features["touched_services"]), "cross-service changes increase risk"),
        ("lint_warnings", float(features["lint_warnings"]), "lint warnings increase risk"),
    ]
    ranked = sorted(signals, key=lambda item: item[1], reverse=True)
    return [
        {
            "feature": feature,
            "value": features[feature],
            "direction": direction,
        }
        for feature, value, direction in ranked[:5]
        if value > 0
    ]
