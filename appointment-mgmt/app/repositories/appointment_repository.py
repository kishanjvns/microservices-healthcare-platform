from typing import Optional
from app.models.request_response_models import AppointmentCreateRequest
from app.config.database import appointment_table
from sqlalchemy import insert
from sqlalchemy.ext.asyncio import AsyncConnection



class appoinment_repository:

    def __init__(self, conn: AsyncConnection):
        self.connection = conn

     
    async def create_appointment(self, appointment_request: dict) -> int:
        query = insert(appointment_table).values(**appointment_request)
        result = await self.connection.execute(query)
        return result.inserted_primary_key[0]
    
    async def fetch_appointment_id(self, appointment_id: int) -> Optional[dict]:
        query = appointment_table.select().where(appointment_table.c.id == appointment_id)
        result = await self.connection.execute(query)
        if result is not None:
            appointment_record = result.fetchone()
            if appointment_record:
                return dict(appointment_record._mapping)
        return None