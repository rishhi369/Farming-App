from fastapi import APIRouter
from app.schemas.dashboard import DashboardSummary
router = APIRouter()
@router.get("/summary", response_model=DashboardSummary)
async def dashboard_summary() -> DashboardSummary:
    return DashboardSummary(readinessScore=82, placementProbability=76, streakDays=12, activeRoadmapItems=4, metrics=[{"label": "ATS score", "value": 91, "delta": 9}, {"label": "Coding depth", "value": 74, "delta": 11}, {"label": "Interview confidence", "value": 78, "delta": 14}, {"label": "RAG mastery", "value": 86, "delta": 8}], roadmap=[{"day": "Today", "focus": "Resume ATS rewrite and SDE mock", "status": "active"}, {"day": "Tomorrow", "focus": "Graph problems and RAG eval notes", "status": "queued"}], skills=[{"skill": "DSA", "current": 72, "target": 90}, {"skill": "System design", "current": 58, "target": 84}, {"skill": "GenAI", "current": 76, "target": 88}])

