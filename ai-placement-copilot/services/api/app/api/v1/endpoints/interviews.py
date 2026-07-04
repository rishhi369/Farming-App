from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from app.schemas.interview import InterviewEvaluateRequest, InterviewEvaluationResponse, InterviewStartRequest, InterviewStartResponse
from app.services.interview.evaluator import create_interview_session, evaluate_answer
router = APIRouter()
websocket_router = APIRouter()
@router.post("/start", response_model=InterviewStartResponse)
async def start_interview(payload: InterviewStartRequest) -> InterviewStartResponse:
    session_id, question = create_interview_session(payload.mode)
    return InterviewStartResponse(sessionId=session_id, openingQuestion=question)
@router.post("/evaluate", response_model=InterviewEvaluationResponse)
async def evaluate_interview_answer(payload: InterviewEvaluateRequest) -> InterviewEvaluationResponse:
    return evaluate_answer(payload.answer, payload.transcript)
@websocket_router.websocket("/interview/{session_id}")
async def interview_socket(websocket: WebSocket, session_id: str) -> None:
    await websocket.accept()
    await websocket.send_json({"type": "session.ready", "sessionId": session_id})
    try:
        while True:
            payload = await websocket.receive_json()
            evaluation = evaluate_answer(payload.get("answer", ""), payload.get("transcript", []))
            await websocket.send_json({"type": "evaluation", "payload": evaluation.model_dump()})
    except WebSocketDisconnect:
        return

