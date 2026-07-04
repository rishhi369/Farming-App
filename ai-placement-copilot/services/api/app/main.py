from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.v1.router import api_router
from app.core.config import settings
from app.core.logging import configure_logging, logger
from app.core.rate_limit import InMemoryRateLimitMiddleware

@asynccontextmanager
async def lifespan(app: FastAPI):
    configure_logging(settings.log_level)
    settings.upload_dir.mkdir(parents=True, exist_ok=True)
    settings.chroma_persist_dir.mkdir(parents=True, exist_ok=True)
    logger.info("api_starting", app=settings.app_name, env=settings.app_env)
    yield
    logger.info("api_shutdown")

app = FastAPI(title="AI Placement Copilot API", version="1.0.0", description="FastAPI microservice layer for multi-agent placement workflows.", lifespan=lifespan)
app.add_middleware(CORSMiddleware, allow_origins=settings.backend_cors_origins, allow_credentials=True, allow_methods=["*"], allow_headers=["*"])
app.add_middleware(InMemoryRateLimitMiddleware)

@app.get("/health", tags=["health"])
async def root_health() -> dict[str, str]:
    return {"status": "ok", "service": "ai-placement-copilot-api"}

app.include_router(api_router, prefix="/api/v1")

