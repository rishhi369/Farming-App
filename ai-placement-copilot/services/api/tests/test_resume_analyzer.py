from app.services.resume.analyzer import analyze_resume_text

def test_resume_analyzer_scores_matching_keywords() -> None:
    result = analyze_resume_text("Built React FastAPI RAG Docker PostgreSQL platform with tests and CI/CD.", "Need React FastAPI RAG Docker PostgreSQL system design", "resume.pdf")
    assert result.atsScore > 60
    assert "react" in result.matchedKeywords
    assert result.optimizedBullets

