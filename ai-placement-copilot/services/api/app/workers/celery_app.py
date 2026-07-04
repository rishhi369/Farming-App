from celery import Celery
from app.core.config import settings

celery_app = Celery("placement_copilot", broker=settings.redis_url, backend=settings.redis_url, include=["app.workers.tasks"])
celery_app.conf.update(task_serializer="json", result_serializer="json", accept_content=["json"], timezone="Asia/Kolkata", enable_utc=True, task_track_started=True, worker_prefetch_multiplier=1)

