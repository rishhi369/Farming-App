from fastapi import APIRouter
from fastapi.responses import StreamingResponse
from app.schemas.agents import AgentStreamRequest
from app.services.agents.workflows import stream_agent_response
router = APIRouter()
@router.post("/stream")
async def stream_agents(payload: AgentStreamRequest) -> StreamingResponse:
    return StreamingResponse(stream_agent_response(payload.prompt), media_type="text/plain")

