from pydantic import BaseModel

class DocumentIngestResponse(BaseModel):
    documentIds: list[str]
    chunks: int

class RagQueryRequest(BaseModel):
    question: str
    sessionId: str | None = None

class Citation(BaseModel):
    documentName: str
    chunkId: str
    score: float

class RagAnswerResponse(BaseModel):
    answer: str
    citations: list[Citation]

