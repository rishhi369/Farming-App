import re
from collections import Counter
from app.schemas.resume import ResumeAnalysisResponse

PLACEMENT_KEYWORDS = {
    "sde": ["python", "java", "c++", "javascript", "typescript", "react", "next.js", "fastapi"],
    "ai": ["rag", "langgraph", "crewai", "openai", "gemini", "embeddings", "vector", "llm"],
    "cloud": ["docker", "aws", "vercel", "render", "postgresql", "redis", "kubernetes"],
    "core": ["system design", "data structures", "algorithms", "sql", "testing", "ci/cd"],
}

def analyze_resume_text(resume_text: str, job_description: str, file_name: str = "resume.pdf") -> ResumeAnalysisResponse:
    resume_lower = resume_text.lower()
    jd_keywords = _keywords_from_job_description(job_description.lower()) or sorted({k for values in PLACEMENT_KEYWORDS.values() for k in values})
    matched = [keyword for keyword in jd_keywords if keyword in resume_lower]
    missing = [keyword for keyword in jd_keywords if keyword not in resume_lower][:10]
    ats_score = min(98, int((len(matched) / max(len(jd_keywords), 1)) * 75 + _action_verb_score(resume_text) + 10))
    suggestions = ["Add quantified outcomes for projects.", "Mirror critical job-description keywords naturally.", "Keep bullets action-first and evidence-backed."]
    if missing:
        suggestions.insert(1, f"Add evidence for missing skills: {', '.join(missing[:5])}.")
    bullets = [
        f"Built and deployed {_path_label(file_name)} enhancements using {', '.join(matched[:4] or ['Python', 'FastAPI', 'Next.js'])}, improving placement preparation workflows with measurable AI assistance.",
        "Designed RAG-backed AI workflows with retrieval, citations, streaming responses, and guardrails to reduce hallucinated guidance.",
        "Implemented Docker, CI checks, structured logging, database persistence, and API documentation."
    ]
    return ResumeAnalysisResponse(atsScore=ats_score, matchedKeywords=matched[:18], missingSkills=missing, suggestions=suggestions, optimizedBullets=bullets)

def _keywords_from_job_description(text: str) -> list[str]:
    keywords = [keyword for values in PLACEMENT_KEYWORDS.values() for keyword in values if keyword in text]
    tokens = re.findall(r"[a-zA-Z][a-zA-Z+#.]{2,}", text)
    keywords.extend(token.lower() for token, count in Counter(tokens).most_common(20) if count > 1)
    return sorted(set(keywords))

def _action_verb_score(text: str) -> int:
    return sum(3 for verb in ["built", "designed", "implemented", "optimized", "deployed", "automated", "led"] if verb in text.lower())

def _path_label(file_name: str) -> str:
    return file_name.rsplit(".", maxsplit=1)[0].replace("_", " ").replace("-", " ").strip() or "resume"

