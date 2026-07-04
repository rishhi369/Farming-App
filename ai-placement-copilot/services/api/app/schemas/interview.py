from pydantic import BaseModel, Field

class InterviewStartRequest(BaseModel):
    mode: str
    targetCompany: str | None = None

class InterviewStartResponse(BaseModel):
    sessionId: str
    openingQuestion: str

class InterviewEvaluateRequest(BaseModel):
    sessionId: str
    answer: str
    transcript: list[str] = Field(default_factory=list)

class InterviewEvaluationResponse(BaseModel):
    confidenceScore: int = Field(ge=0, le=100)
    communicationScore: int = Field(ge=0, le=100)
    technicalScore: int = Field(ge=0, le=100)
    feedback: list[str]
    nextQuestion: str

