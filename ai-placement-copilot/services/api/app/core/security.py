from datetime import UTC, datetime, timedelta
from typing import Any
from fastapi import Depends, HTTPException, Request, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jose import JWTError, jwt
from pydantic import BaseModel
from app.core.config import settings

bearer_scheme = HTTPBearer(auto_error=False)

class Principal(BaseModel):
    user_id: str
    email: str
    full_name: str | None = None
    claims: dict[str, Any] = {}

def create_access_token(subject: str, email: str, expires_delta: timedelta | None = None) -> str:
    expire = datetime.now(UTC) + (expires_delta or timedelta(minutes=settings.access_token_expire_minutes))
    return jwt.encode({"sub": subject, "email": email, "exp": expire}, settings.jwt_secret_key, algorithm=settings.jwt_algorithm)

async def get_current_principal(request: Request, credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme)) -> Principal:
    if not credentials:
        return Principal(user_id=request.headers.get("x-demo-user-id", "demo-user"), email=request.headers.get("x-demo-user-email", "student@example.com"), full_name="Demo Student")
    try:
        payload = jwt.decode(credentials.credentials, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm], options={"verify_aud": False})
    except JWTError as exc:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid authentication token") from exc
    subject = payload.get("sub")
    if subject is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token is missing subject")
    return Principal(user_id=str(subject), email=payload.get("email", "student@example.com"), full_name=payload.get("name"), claims=payload)

