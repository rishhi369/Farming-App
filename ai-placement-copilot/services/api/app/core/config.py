from functools import lru_cache
from pathlib import Path
from pydantic import AnyHttpUrl, Field, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore", case_sensitive=False)
    app_name: str = "AI Placement Copilot"
    app_env: str = "development"
    log_level: str = "INFO"
    backend_cors_origins: list[str] = Field(default_factory=lambda: ["http://localhost:3000"])
    database_url: str = "sqlite+aiosqlite:///./placement_copilot.db"
    redis_url: str = "redis://localhost:6379/0"
    upload_dir: Path = Path("storage/uploads")
    chroma_persist_dir: Path = Path("storage/chroma")
    jwt_secret_key: str = "change-me"
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 120
    clerk_jwks_url: AnyHttpUrl | None = None
    ai_provider: str = "openai"
    enable_real_llm: bool = False
    enable_crewai: bool = False
    enable_semantic_cache: bool = True
    openai_api_key: str | None = None
    openai_model: str = "gpt-4o-mini"
    openai_embedding_model: str = "text-embedding-3-small"
    gemini_api_key: str | None = None
    gemini_model: str = "gemini-1.5-pro"
    ollama_base_url: str = "http://localhost:11434"
    ollama_model: str = "llama3.1"
    rate_limit_per_minute: int = 90

    @field_validator("backend_cors_origins", mode="before")
    @classmethod
    def assemble_cors_origins(cls, value: str | list[str]) -> list[str]:
        if isinstance(value, str):
            return [origin.strip() for origin in value.split(",") if origin.strip()]
        return value

    @field_validator("clerk_jwks_url", mode="before")
    @classmethod
    def empty_url_to_none(cls, value: str | None) -> str | None:
        return value or None

@lru_cache
def get_settings() -> Settings:
    return Settings()

settings = get_settings()

