import asyncio
import json
from collections.abc import AsyncIterator
from typing import Any
from app.core.config import settings
from app.core.logging import logger

class LLMClient:
    async def generate_text(self, system: str, user: str) -> str:
        if settings.enable_real_llm and settings.ai_provider == "openai" and settings.openai_api_key:
            try:
                from openai import AsyncOpenAI
                client = AsyncOpenAI(api_key=settings.openai_api_key)
                response = await client.chat.completions.create(model=settings.openai_model, messages=[{"role": "system", "content": system}, {"role": "user", "content": user}], temperature=0.35)
                return response.choices[0].message.content or ""
            except Exception as exc:
                logger.warning("openai_generation_failed", error=str(exc))
        return self._fallback_text(user)

    async def generate_json(self, system: str, user: str, fallback: dict[str, Any]) -> dict[str, Any]:
        try:
            return json.loads(await self.generate_text(system, user))
        except json.JSONDecodeError:
            return fallback

    async def stream_text(self, prompt: str) -> AsyncIterator[str]:
        fallback = self._fallback_text(prompt)
        for token in fallback.split(" "):
            await asyncio.sleep(0.015)
            yield token + " "

    def _fallback_text(self, prompt: str) -> str:
        return "Plan: clarify the target role, prioritize high-signal gaps, practice one measurable interview loop, and ship proof-of-work evidence. Prompt focus: " + prompt[:180]

llm_client = LLMClient()

