from __future__ import annotations

import argparse
import json
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from app.feature_engineering import ALL_FEATURES, CATEGORICAL_FEATURES, NUMERIC_FEATURES
from training.generate_sample_data import make_sample_data


TARGET = "build_failed"


def _require_training_dependencies() -> None:
    missing = []
    for module in ["joblib", "imblearn", "optuna", "sklearn", "xgboost"]:
        try:
            __import__(module)
        except ImportError:
            missing.append(module)
    if missing:
        raise SystemExit(
            "Missing training dependencies: "
            + ", ".join(missing)
            + ". Run `pip install -r requirements.txt` from the project folder."
        )


def load_training_frame(path: Path) -> Any:
    import pandas as pd

    if path.exists():
        return pd.read_csv(path)
    path.parent.mkdir(parents=True, exist_ok=True)
    frame = make_sample_data(rows=3000)
    frame.to_csv(path, index=False)
    return frame


def build_preprocessor() -> Any:
    from sklearn.compose import ColumnTransformer
    from sklearn.impute import SimpleImputer
    from sklearn.pipeline import Pipeline
    from sklearn.preprocessing import OneHotEncoder, StandardScaler

    numeric_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler()),
        ]
    )
    categorical_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="most_frequent")),
            ("onehot", OneHotEncoder(handle_unknown="ignore", sparse_output=False)),
        ]
    )

    return ColumnTransformer(
        transformers=[
            ("numeric", numeric_pipeline, NUMERIC_FEATURES),
            ("categorical", categorical_pipeline, CATEGORICAL_FEATURES),
        ],
        sparse_threshold=0,
    )


def build_ensemble(params: dict[str, Any], seed: int) -> Any:
    from imblearn.over_sampling import ADASYN
    from imblearn.pipeline import Pipeline as ImbPipeline
    from sklearn.ensemble import RandomForestClassifier, VotingClassifier
    from xgboost import XGBClassifier

    xgb = XGBClassifier(
        n_estimators=params.get("xgb_n_estimators", 260),
        max_depth=params.get("xgb_max_depth", 4),
        learning_rate=params.get("xgb_learning_rate", 0.045),
        subsample=params.get("xgb_subsample", 0.9),
        colsample_bytree=params.get("xgb_colsample_bytree", 0.85),
        reg_lambda=params.get("xgb_reg_lambda", 1.4),
        min_child_weight=params.get("xgb_min_child_weight", 2),
        eval_metric="logloss",
        tree_method="hist",
        n_jobs=-1,
        random_state=seed,
    )
    rf = RandomForestClassifier(
        n_estimators=params.get("rf_n_estimators", 360),
        max_depth=params.get("rf_max_depth", 14),
        min_samples_split=params.get("rf_min_samples_split", 4),
        min_samples_leaf=params.get("rf_min_samples_leaf", 2),
        class_weight="balanced_subsample",
        n_jobs=-1,
        random_state=seed,
    )
    ensemble = VotingClassifier(
        estimators=[("xgb", xgb), ("rf", rf)],
        voting="soft",
        weights=[params.get("xgb_weight", 2), 1],
        n_jobs=1,
    )

    return ImbPipeline(
        steps=[
            ("preprocess", build_preprocessor()),
            ("balance", ADASYN(random_state=seed, n_neighbors=5)),
            ("model", ensemble),
        ]
    )


def tune_params(X_train: Any, y_train: Any, trials: int, seed: int) -> dict[str, Any]:
    if trials <= 0:
        return {}

    import numpy as np
    import optuna
    from sklearn.model_selection import StratifiedKFold, cross_val_score

    def objective(trial: Any) -> float:
        params = {
            "xgb_n_estimators": trial.suggest_int("xgb_n_estimators", 180, 420),
            "xgb_max_depth": trial.suggest_int("xgb_max_depth", 3, 7),
            "xgb_learning_rate": trial.suggest_float("xgb_learning_rate", 0.02, 0.12, log=True),
            "xgb_subsample": trial.suggest_float("xgb_subsample", 0.72, 1.0),
            "xgb_colsample_bytree": trial.suggest_float("xgb_colsample_bytree", 0.70, 1.0),
            "xgb_reg_lambda": trial.suggest_float("xgb_reg_lambda", 0.4, 4.0, log=True),
            "xgb_min_child_weight": trial.suggest_int("xgb_min_child_weight", 1, 6),
            "rf_n_estimators": trial.suggest_int("rf_n_estimators", 220, 520),
            "rf_max_depth": trial.suggest_int("rf_max_depth", 8, 22),
            "rf_min_samples_split": trial.suggest_int("rf_min_samples_split", 2, 8),
            "rf_min_samples_leaf": trial.suggest_int("rf_min_samples_leaf", 1, 4),
            "xgb_weight": trial.suggest_int("xgb_weight", 1, 3),
        }
        model = build_ensemble(params, seed)
        cv = StratifiedKFold(n_splits=3, shuffle=True, random_state=seed)
        scores = cross_val_score(model, X_train, y_train, scoring="roc_auc", cv=cv, n_jobs=1)
        return float(np.mean(scores))

    study = optuna.create_study(direction="maximize", study_name="cicd_failure_prediction")
    study.optimize(objective, n_trials=trials, show_progress_bar=False)
    return dict(study.best_params)


def evaluate_model(model: Any, X_test: Any, y_test: Any, threshold: float | None = None) -> dict[str, Any]:
    import numpy as np
    from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score, roc_auc_score

    probabilities = model.predict_proba(X_test)[:, 1]
    if threshold is None:
        threshold = choose_threshold(y_test, probabilities)
    predictions = (probabilities >= threshold).astype(int)
    return {
        "accuracy": round(float(accuracy_score(y_test, predictions)), 4),
        "precision": round(float(precision_score(y_test, predictions, zero_division=0)), 4),
        "recall": round(float(recall_score(y_test, predictions, zero_division=0)), 4),
        "f1": round(float(f1_score(y_test, predictions, zero_division=0)), 4),
        "roc_auc": round(float(roc_auc_score(y_test, probabilities)), 4),
        "threshold": round(float(threshold), 4),
        "positive_rate": round(float(np.mean(y_test)), 4),
    }


def choose_threshold(y_true: Any, probabilities: Any) -> float:
    import numpy as np
    from sklearn.metrics import f1_score

    best_threshold = 0.5
    best_score = -1.0
    for threshold in np.linspace(0.25, 0.82, 58):
        score = f1_score(y_true, probabilities >= threshold, zero_division=0)
        if score > best_score:
            best_score = score
            best_threshold = float(threshold)
    return best_threshold


def baseline_metrics(X_train: Any, X_test: Any, y_train: Any, y_test: Any, seed: int) -> dict[str, Any]:
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.pipeline import Pipeline

    baseline = Pipeline(
        steps=[
            ("preprocess", build_preprocessor()),
            (
                "model",
                RandomForestClassifier(
                    n_estimators=120,
                    max_depth=8,
                    random_state=seed,
                    n_jobs=-1,
                ),
            ),
        ]
    )
    baseline.fit(X_train, y_train)
    return evaluate_model(baseline, X_test, y_test)


def train(args: argparse.Namespace) -> dict[str, Any]:
    _require_training_dependencies()

    import joblib
    from sklearn.model_selection import train_test_split

    frame = load_training_frame(Path(args.data))
    missing = [column for column in ALL_FEATURES + [TARGET] if column not in frame.columns]
    if missing:
        raise SystemExit(f"Training data is missing required columns: {missing}")

    X = frame[ALL_FEATURES]
    y = frame[TARGET].astype(int)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=args.test_size, random_state=args.seed, stratify=y
    )

    baseline = baseline_metrics(X_train, X_test, y_train, y_test, args.seed)
    best_params = tune_params(X_train, y_train, args.trials, args.seed)
    model = build_ensemble(best_params, args.seed)
    model.fit(X_train, y_train)
    metrics = evaluate_model(model, X_test, y_test)

    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    bundle = {
        "model": model,
        "feature_names": ALL_FEATURES,
        "threshold": metrics["threshold"],
        "metrics": {"baseline": baseline, "ensemble": metrics},
        "metadata": {
            "model": "xgb_rf_adasyn_optuna",
            "trained_at": datetime.now(timezone.utc).isoformat(),
            "rows": int(len(frame)),
            "optuna_trials": int(args.trials),
            "target": TARGET,
            "best_params": best_params,
        },
    }
    joblib.dump(bundle, output)

    result = {
        "output": str(output),
        "failure_rate": round(float(y.mean()), 4),
        "baseline": baseline,
        "ensemble": metrics,
        "relative_accuracy_improvement": round(
            (metrics["accuracy"] - baseline["accuracy"]) / max(baseline["accuracy"], 1e-9), 4
        ),
    }
    print(json.dumps(result, indent=2))
    return result


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Train the CI/CD build failure prediction model.")
    parser.add_argument("--data", default="data/sample_builds.csv")
    parser.add_argument("--output", default="artifacts/model.joblib")
    parser.add_argument("--trials", type=int, default=10, help="Optuna trials. Use 0 for fastest training.")
    parser.add_argument("--test-size", type=float, default=0.2)
    parser.add_argument("--seed", type=int, default=42)
    return parser.parse_args()


if __name__ == "__main__":
    train(parse_args())
