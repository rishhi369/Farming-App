import uuid
from app.schemas.interview import InterviewEvaluationResponse

OPENING_QUESTIONS = {
    "sde": "Design a rate-limited job application tracker. Which APIs, schema, and failure modes matter?",
    "data analyst": "A placement funnel drops after resume screening. How would you investigate with SQL and dashboards?",
    "devops": "How would you deploy this platform with blue-green releases, secrets, and observability?",
    "genai engineer": "Design a RAG assistant for interview preparation with citations and hallucination controls.",
}

def create_interview_session(mode: str) -> tuple[str, str]:
    return str(uuid.uuid4()), OPENING_QUESTIONS.get(mode.lower(), f"Start a {mode} mock interview by walking through a recent project.")

def evaluate_answer(answer: str, transcript: list[str], mode: str = "general") -> InterviewEvaluationResponse:
    words = answer.split()
    has_metrics = any(char.isdigit() for char in answer)
    has_structure = any(marker in answer.lower() for marker in ["first", "second", "tradeoff", "because"])
    communication = min(96, 55 + len(words) // 4 + (12 if has_structure else 0))
    technical = min(96, 58 + (14 if has_metrics else 0) + (16 if len(words) > 65 else 0))
    confidence = min(96, int((communication + technical) / 2) + (5 if len(transcript) > 2 else 0))
    return InterviewEvaluationResponse(confidenceScore=confidence, communicationScore=communication, technicalScore=technical, feedback=["Use context, decision, tradeoff, measurable result.", "Add production constraints and validation.", "Close with tests, evals, monitoring, or user impact."], nextQuestion=_next_question(mode, answer))

def _next_question(mode: str, answer: str) -> str:
    lower = f"{mode} {answer}".lower()
    if "rag" in lower or "genai" in lower:
        return "How would you evaluate retrieval quality and reduce hallucinations before launch?"
    if "sql" in lower or "analyst" in lower:
        return "Which SQL metrics would you track and why?"
    if "deploy" in lower or "devops" in lower:
        return "What rollback strategy and health checks would you configure?"
    return "What is the biggest tradeoff in your approach, and how would you test it?"

