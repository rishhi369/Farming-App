from fastapi import APIRouter
from app.schemas.coding import CodeReviewRequest, CodeReviewResponse
from app.services.coding.reviewer import review_code
router = APIRouter()
@router.post("/review", response_model=CodeReviewResponse)
async def review(payload: CodeReviewRequest) -> CodeReviewResponse:
    return review_code(payload.language, payload.problem, payload.code)

