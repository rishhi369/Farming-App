from pydantic import BaseModel

class AgentStreamRequest(BaseModel):
    prompt: str
    sessionId: str

