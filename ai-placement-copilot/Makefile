.PHONY: dev api web worker test lint docker-up docker-down migrate

dev:
	pnpm dev

api:
	cd services/api && uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

web:
	pnpm --filter @placement-copilot/web dev

worker:
	cd services/api && celery -A app.workers.celery_app.celery_app worker --loglevel=INFO

test:
	cd services/api && pytest
	pnpm --filter @placement-copilot/web typecheck

lint:
	cd services/api && ruff check app tests
	pnpm --filter @placement-copilot/web lint

migrate:
	cd services/api && alembic upgrade head

docker-up:
	docker compose up --build

docker-down:
	docker compose down

