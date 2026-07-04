from pydantic import BaseModel

class CodeReviewRequest(BaseModel):
    language: str
    problem: str
    code: str

class CodeReviewResponse(BaseModel):
    verdict: str
    bugs: list[str]
    optimizations: list[str]
    timeComplexity: str
    spaceComplexity: str
    followUps: list[str]

