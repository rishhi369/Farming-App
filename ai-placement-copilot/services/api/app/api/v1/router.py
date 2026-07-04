from fastapi import APIRouter
from app.api.v1.endpoints import agents, coding, dashboard, health, interviews, rag, resume

api_router = APIRouter()
api_router.include_router(health.router, tags=["health"])
api_router.include_router(dashboard.router, prefix="/dashboard", tags=["dashboard"])
api_router.include_router(resume.router, prefix="/resume", tags=["resume"])
api_router.include_router(interviews.router, prefix="/interviews", tags=["interviews"])
api_router.include_router(coding.router, prefix="/coding", tags=["coding"])
api_router.include_router(rag.router, prefix="/rag", tags=["rag"])
api_router.include_router(agents.router, prefix="/agents", tags=["agents"])
api_router.include_router(interviews.websocket_router, prefix="/ws", tags=["websocket"])

