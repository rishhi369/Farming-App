from fastapi import Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.security import Principal, get_current_principal
from app.db.models import User
from app.db.session import get_db_session

async def get_or_create_user(principal: Principal = Depends(get_current_principal), session: AsyncSession = Depends(get_db_session)) -> User:
    result = await session.execute(select(User).where(User.email == principal.email))
    user = result.scalar_one_or_none()
    if user:
        return user
    user = User(id=principal.user_id, email=principal.email, full_name=principal.full_name)
    session.add(user)
    await session.commit()
    await session.refresh(user)
    return user

