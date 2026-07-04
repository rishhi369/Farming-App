from pydantic import BaseModel, Field

class ResumeAnalysisResponse(BaseModel):
    atsScore: int = Field(ge=0, le=100)
    matchedKeywords: list[str]
    missingSkills: list[str]
    suggestions: list[str]
    optimizedBullets: list[str]

