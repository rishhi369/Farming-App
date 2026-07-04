# CI/CD Build Failure Detection - GitHub Bot

Resume-ready project for predicting GitHub Actions build failures before a pipeline wastes runner time. It includes a Flask REST API, GitHub webhook bot, feature engineering, ensemble ML training with Random Forest + XGBoost, ADASYN imbalance handling, Optuna tuning, Docker, GitHub Actions, and Cloud Run deployment files.

Important: use the sample data for demos, but only claim production metrics after running the training pipeline on your own CI history.

## What It Does

- Predicts build failure probability from pull request, workflow, and repository signals.
- Exposes `/predict`, `/health`, `/model`, and `/github/webhook` endpoints.
- Verifies GitHub webhook signatures with `X-Hub-Signature-256`.
- Posts commit status back to GitHub when `GITHUB_TOKEN` is configured.
- Trains an ensemble pipeline using Scikit-learn, Random Forest, XGBoost, ADASYN, and Optuna.
- Falls back to a deterministic heuristic model if no trained artifact exists, so the API works immediately for demos.

## Quick Start

```powershell
cd C:\Users\dipak\Farming-App\cicd-build-failure-bot
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python training/generate_sample_data.py --rows 3000 --output data/sample_builds.csv
python training/train.py --data data/sample_builds.csv --trials 10 --output artifacts/model.joblib
flask --app app.main run --host 0.0.0.0 --port 8080
```

Try a prediction:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/predict `
  -ContentType "application/json" `
  -Body '{"changed_files":42,"additions":1200,"deletions":300,"commits":6,"previous_failed_runs_7d":3,"flaky_tests_7d":4,"test_count":620,"failed_tests":2,"lint_warnings":18,"dependency_files_changed":1,"touched_services":5,"parallel_jobs":8,"cache_hit_rate":0.31,"branch":"feature/payment-refactor","event_name":"pull_request","repo_language":"Python","runner_os":"ubuntu-latest","workflow_name":"ci"}'
```

## API

### `GET /health`

Returns service status and whether a trained model artifact was loaded.

### `POST /predict`

Accepts either a flat JSON feature object or `{ "features": { ... } }`.

Response:

```json
{
  "failure_probability": 0.82,
  "risk_level": "high",
  "should_warn": true,
  "threshold": 0.62,
  "model": "xgb_rf_adasyn_optuna",
  "top_signals": []
}
```

### `POST /github/webhook`

Supports `pull_request`, `workflow_run`, `workflow_job`, and `check_suite` events. Configure the webhook URL as:

```text
https://YOUR_SERVICE_URL/github/webhook
```

Set these environment variables:

```text
GITHUB_WEBHOOK_SECRET=your-webhook-secret
GITHUB_TOKEN=github_pat_or_app_token
GITHUB_STATUS_CONTEXT=ci-failure-predictor
BOT_DRY_RUN=false
```

## Training

Generate demo data:

```powershell
python training/generate_sample_data.py --rows 5000 --output data/sample_builds.csv
```

Train with tuning:

```powershell
python training/train.py --data data/sample_builds.csv --trials 25 --output artifacts/model.joblib
```

Train fast without Optuna:

```powershell
python training/train.py --data data/sample_builds.csv --trials 0 --output artifacts/model.joblib
```

The saved artifact contains the preprocessing pipeline, ADASYN sampler, ensemble classifier, optimized threshold, metrics, and metadata.

## Deploy To GCP Cloud Run

```powershell
gcloud builds submit --config cloudbuild.yaml --substitutions _SERVICE_NAME=cicd-failure-bot,_REGION=asia-south1
gcloud run services describe cicd-failure-bot --region asia-south1 --format "value(status.url)"
```

Then add the returned URL to your GitHub repository webhook settings.

## Suggested Resume Bullets

- Built a real-time CI/CD build failure prediction bot using Python, Flask, Scikit-learn, XGBoost, Random Forest, ADASYN, Optuna, GitHub Actions, REST APIs, Docker, and GCP Cloud Run.
- Engineered GitHub webhook features from pull requests and workflow events, then served risk scores through a low-latency REST API with GitHub commit status feedback.
- Improved failure prediction accuracy after ensemble tuning and imbalance handling; report the exact percentage from your own `training/train.py` output.

## Project Structure

```text
cicd-build-failure-bot/
  app/                  Flask API, webhook handling, GitHub REST client
  training/             sample data generator and ML training pipeline
  tests/                lightweight API and feature tests
  deploy/gcp/           Cloud Run service template
  .github/workflows/    standalone CI workflow
  artifacts/            trained model output location
```
