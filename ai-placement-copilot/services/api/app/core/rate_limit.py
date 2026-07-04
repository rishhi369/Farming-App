import time
from collections.abc import Awaitable, Callable
from fastapi import Request, Response
from starlette.middleware.base import BaseHTTPMiddleware
from app.core.config import settings

class InMemoryRateLimitMiddleware(BaseHTTPMiddleware):
    def __init__(self, app):
        super().__init__(app)
        self._hits: dict[str, list[float]] = {}

    async def dispatch(self, request: Request, call_next: Callable[[Request], Awaitable[Response]]) -> Response:
        if request.url.path in {"/health", "/docs", "/openapi.json"}:
            return await call_next(request)
        key = request.client.host if request.client else "unknown"
        now = time.time()
        hits = [hit for hit in self._hits.get(key, []) if hit >= now - 60]
        if len(hits) >= settings.rate_limit_per_minute:
            return Response("Rate limit exceeded", status_code=429)
        hits.append(now)
        self._hits[key] = hits
        return await call_next(request)

