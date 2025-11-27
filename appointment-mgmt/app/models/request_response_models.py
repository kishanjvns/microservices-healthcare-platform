from pydantic import BaseModel, Field
from uuid import UUID
from typing import Optional, Any
from datetime import datetime

class AppointmentCreateRequest(BaseModel):
    patient_id: UUID
    patient_name: str
    doctor_id: UUID
    doctor_name: str
    appointment_date_time: datetime
    reason: str = None

class AppointmentGetResponse(AppointmentCreateRequest):
    appointment_id: int = Field(..., alias="id") # database column is 'id', but we want to expose it as 'appointment_id'
    status: str

class ApplicationBaseResponse(BaseModel):
    status: int
    message: str
    data: Optional[dict[str, Any]] = None
