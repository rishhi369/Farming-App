# AI Placement Copilot

Enterprise-grade full-stack multi-agent Generative AI platform for placement preparation. The repo is structured to demonstrate full-stack engineering, GenAI/RAG architecture, agent orchestration, cloud deployment, and recruiter-ready product thinking.

## Included

- Next.js 15 + TypeScript frontend with App Router, Clerk-ready auth, glassmorphism UI, dark mode, responsive dashboards, charts, AI typing effects, Monaco editor, uploads, and streaming UI.
- FastAPI backend with modular routes, Swagger docs, JWT auth hook, REST + WebSocket APIs, file uploads, structured logging, rate limiting, Celery tasks, and tests.
- LangGraph workflow for Research, Resume, Interview, Coding, and Career Roadmap agents, with CrewAI adapter flag.
- RAG with PDF/DOCX/text ingestion, chunking, ChromaDB persistent vector store, retrieval, citations, and guarded no-context behavior.
- PostgreSQL schema for users, resumes, interviews, chat sessions, uploaded documents, analytics, and feedback reports.
- Redis for Celery, cache extension points, and production rate-limit expansion.
- Docker Compose, GitHub Actions, Vercel config, Render blueprint, and AWS App Runner starter template.

## Architecture

```mermaid
flowchart LR
  "Student" --> "Next.js 15 Web App"
  "Next.js 15 Web App" -->|"REST uploads + JSON"| "FastAPI API"
  "Next.js 15 Web App" -->|"WebSocket + streaming"| "Interview/Agent Gateway"
  "FastAPI API" --> "PostgreSQL"
  "FastAPI API" --> "Redis"
  "FastAPI API" --> "Celery Worker"
  "FastAPI API" --> "LangGraph Agent Workflow"
  "LangGraph Agent Workflow" --> "Research Agent"
  "LangGraph Agent Workflow" --> "Resume Agent"
  "LangGraph Agent Workflow" --> "Interview Agent"
  "LangGraph Agent Workflow" --> "Coding Agent"
  "LangGraph Agent Workflow" --> "Career Roadmap Agent"
  "FastAPI API" --> "RAG Service"
  "RAG Service" --> "ChromaDB"
  "RAG Service" --> "OpenAI/Gemini/Ollama"
```

## Folder Structure

```text
ai-placement-copilot/
  apps/web/                 Next.js 15 TypeScript frontend
  services/api/             FastAPI backend
  services/api/app/api/     REST and WebSocket routes
  services/api/app/db/      SQLAlchemy models and sessions
  services/api/app/services Agent, RAG, resume, interview, coding logic
  services/api/app/workers  Celery app and async jobs
  services/api/migrations   Alembic migrations
  infra/                    Vercel, Render, AWS deployment templates
  docker-compose.yml        Local Postgres, Redis, API, worker, web stack
```

## Feature Map

| Feature | Implementation |
| --- | --- |
| Resume Analyzer Agent | `/resume` UI + `POST /api/v1/resume/analyze`; PDF/DOCX text extraction, ATS score, skills, suggestions, bullets |
| AI Interview Agent | `/interview` UI + `POST /api/v1/interviews/start`, `POST /api/v1/interviews/evaluate`, `WS /api/v1/ws/interview/{id}` |
| Coding Interview Copilot | `/coding` UI with Monaco + `POST /api/v1/coding/review`; bugs, complexity, optimizations, follow-ups |
| RAG Knowledge Assistant | `/knowledge` UI + `POST /api/v1/rag/documents`, `POST /api/v1/rag/query`; ChromaDB retrieval and citations |
| Multi-Agent Architecture | `app/services/agents/workflows.py`; LangGraph graph with memory/tool-calling extension points |
| Career Dashboard | `/dashboard`; readiness score, charts, roadmap, streaming agent console |

## Local Setup

```bash
cp .env.example .env
docker compose up --build
```

Open:

- Web app: `http://localhost:3000`
- API Swagger docs: `http://localhost:8000/docs`
- API health: `http://localhost:8000/health`

The project runs with deterministic local AI fallbacks by default. Set `ENABLE_REAL_LLM=true` and provide `OPENAI_API_KEY` or `GEMINI_API_KEY` to call hosted models.

## Manual Development

Frontend:

```bash
pnpm install --no-frozen-lockfile
pnpm --filter @placement-copilot/web dev
```

Backend:

```bash
cd services/api
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Checks:

```bash
pnpm --filter @placement-copilot/web typecheck
pnpm --filter @placement-copilot/web lint
cd services/api
ruff check app tests
pytest
```

## Environment Variables

Use `.env.example` as the source of truth.

- `NEXT_PUBLIC_API_BASE_URL` and `NEXT_PUBLIC_WS_BASE_URL` connect the web app to FastAPI.
- `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY` and `CLERK_SECRET_KEY` enable Clerk auth. Without real keys, the app runs in local demo auth mode.
- `DATABASE_URL` points to PostgreSQL.
- `REDIS_URL` powers Celery and cache extension points.
- `CHROMA_PERSIST_DIR` stores vector indexes.
- `ENABLE_REAL_LLM`, `OPENAI_API_KEY`, `GEMINI_API_KEY`, and `OLLAMA_BASE_URL` control AI providers.

## Deployment

Frontend on Vercel:

1. Import the repo and set root directory to `ai-placement-copilot`.
2. Use `infra/vercel.json`.
3. Configure API, WebSocket, and Clerk environment variables.

Backend on Render:

1. Create a Blueprint from `infra/render.yaml`.
2. Add secrets for JWT and AI provider keys.
3. Point the frontend environment variables to the Render API URL.

AWS:

1. Build and push `services/api` to ECR.
2. Deploy `infra/aws/apprunner.yaml` with RDS PostgreSQL and ElastiCache Redis URLs.
3. Deploy the web app on Vercel, Amplify, or CloudFront.

## Guardrails

- RAG answers cite retrieved chunks and return an explicit no-documents response when context is absent.
- LLM calls are behind `LLMClient`, making provider swaps and local fallbacks straightforward.
- Prompt templates live in `app/services/ai/prompts.py`.
- Structured logs use JSON via `structlog`.
- Alembic migrations define the production database schema.
- Tests cover API health and resume scoring behavior.

