import uuid
from datetime import datetime
from sqlalchemy import DateTime, Float, ForeignKey, Integer, String, func
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.types import JSON
from app.db.base import Base

def uuid_str() -> str:
    return str(uuid.uuid4())

JsonType = JSON().with_variant(JSONB(), "postgresql")

class TimestampMixin:
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

class User(Base, TimestampMixin):
    __tablename__ = "users"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    email: Mapped[str] = mapped_column(String(255), unique=True, index=True)
    full_name: Mapped[str | None] = mapped_column(String(255))
    role: Mapped[str] = mapped_column(String(40), default="student")
    resumes: Mapped[list["Resume"]] = relationship(back_populates="user")
    interviews: Mapped[list["Interview"]] = relationship(back_populates="user")

class Resume(Base, TimestampMixin):
    __tablename__ = "resumes"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    file_name: Mapped[str] = mapped_column(String(255))
    ats_score: Mapped[float] = mapped_column(Float, default=0)
    analysis: Mapped[dict] = mapped_column(JsonType, default=dict)
    user: Mapped[User] = relationship(back_populates="resumes")

class Interview(Base, TimestampMixin):
    __tablename__ = "interviews"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    mode: Mapped[str] = mapped_column(String(80))
    transcript: Mapped[list] = mapped_column(JsonType, default=list)
    evaluation: Mapped[dict] = mapped_column(JsonType, default=dict)
    user: Mapped[User] = relationship(back_populates="interviews")

class ChatSession(Base, TimestampMixin):
    __tablename__ = "chat_sessions"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    title: Mapped[str] = mapped_column(String(255))
    messages: Mapped[list] = mapped_column(JsonType, default=list)

class UploadedDocument(Base, TimestampMixin):
    __tablename__ = "uploaded_documents"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    file_name: Mapped[str] = mapped_column(String(255))
    mime_type: Mapped[str | None] = mapped_column(String(120))
    vector_collection: Mapped[str] = mapped_column(String(120))
    chunk_count: Mapped[int] = mapped_column(Integer, default=0)

class Analytics(Base, TimestampMixin):
    __tablename__ = "analytics"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    metric: Mapped[str] = mapped_column(String(120))
    value: Mapped[float] = mapped_column(Float)
    meta: Mapped[dict] = mapped_column("metadata", JsonType, default=dict)

class FeedbackReport(Base, TimestampMixin):
    __tablename__ = "feedback_reports"
    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=uuid_str)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    report_type: Mapped[str] = mapped_column(String(80))
    payload: Mapped[dict] = mapped_column(JsonType, default=dict)

