"""initial schema"""
from collections.abc import Sequence
from alembic import op
import sqlalchemy as sa

revision: str = "0001_initial"
down_revision: str | None = None
branch_labels: str | Sequence[str] | None = None
depends_on: str | Sequence[str] | None = None

def upgrade() -> None:
    op.create_table("users", sa.Column("id", sa.String(36), primary_key=True), sa.Column("email", sa.String(255), nullable=False, unique=True, index=True), sa.Column("full_name", sa.String(255)), sa.Column("role", sa.String(40), nullable=False, server_default="student"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("resumes", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("file_name", sa.String(255), nullable=False), sa.Column("ats_score", sa.Float(), nullable=False, server_default="0"), sa.Column("analysis", sa.JSON(), nullable=False, server_default="{}"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("interviews", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("mode", sa.String(80), nullable=False), sa.Column("transcript", sa.JSON(), nullable=False, server_default="[]"), sa.Column("evaluation", sa.JSON(), nullable=False, server_default="{}"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("chat_sessions", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("title", sa.String(255), nullable=False), sa.Column("messages", sa.JSON(), nullable=False, server_default="[]"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("uploaded_documents", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("file_name", sa.String(255), nullable=False), sa.Column("mime_type", sa.String(120)), sa.Column("vector_collection", sa.String(120), nullable=False), sa.Column("chunk_count", sa.Integer(), nullable=False, server_default="0"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("analytics", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("metric", sa.String(120), nullable=False), sa.Column("value", sa.Float(), nullable=False), sa.Column("metadata", sa.JSON(), nullable=False, server_default="{}"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))
    op.create_table("feedback_reports", sa.Column("id", sa.String(36), primary_key=True), sa.Column("user_id", sa.String(36), sa.ForeignKey("users.id"), nullable=False), sa.Column("report_type", sa.String(80), nullable=False), sa.Column("payload", sa.JSON(), nullable=False, server_default="{}"), sa.Column("created_at", sa.DateTime(timezone=True), server_default=sa.func.now()), sa.Column("updated_at", sa.DateTime(timezone=True), server_default=sa.func.now()))

def downgrade() -> None:
    for table in ["feedback_reports", "analytics", "uploaded_documents", "chat_sessions", "interviews", "resumes", "users"]:
        op.drop_table(table)

