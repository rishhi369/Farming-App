from pathlib import Path
from fastapi import APIRouter, BackgroundTasks, File, Form, UploadFile
from app.core.config import settings
from app.schemas.resume import ResumeAnalysisResponse
from app.services.resume.analyzer import analyze_resume_text
from app.services.resume.parser import extract_resume_text
from app.workers.tasks import record_usage_event
router = APIRouter()
@router.post("/analyze", response_model=ResumeAnalysisResponse)
async def analyze_resume(background_tasks: BackgroundTasks, file: UploadFile = File(...), job_description: str = Form(...)) -> ResumeAnalysisResponse:
    content = await file.read()
    settings.upload_dir.mkdir(parents=True, exist_ok=True)
    Path(settings.upload_dir, file.filename).write_bytes(content)
    analysis = analyze_resume_text(extract_resume_text(file.filename, content), job_description, file.filename)
    background_tasks.add_task(record_usage_event, "resume_analysis", {"file": file.filename})
    return analysis

