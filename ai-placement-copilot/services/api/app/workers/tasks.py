from app.core.logging import logger
from app.workers.celery_app import celery_app

@celery_app.task(name="record_usage_event")
def record_usage_event(event_name: str, payload: dict) -> dict:
    logger.info("usage_event", event_name=event_name, payload=payload)
    return {"event": event_name, "recorded": True}

@celery_app.task(name="generate_daily_roadmap")
def generate_daily_roadmap(user_id: str) -> dict:
    return {"user_id": user_id, "items": ["Review one resume bullet.", "Solve one DSA pattern.", "Run one adaptive mock interview."]}

