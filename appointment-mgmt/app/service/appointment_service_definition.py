
from abc import ABC, abstractmethod
from app.models.request_response_models import AppointmentCreateRequest
from app.models.request_response_models import ApplicationBaseResponse
from app.models.request_response_models import AppointmentGetResponse


class AppointmentService(ABC):
    
    @abstractmethod
    async def save_appointment(self, appointment_request: AppointmentCreateRequest) -> ApplicationBaseResponse:
        pass

    @abstractmethod
    async def fetch_appointment_id(self, appointment_id: int) -> AppointmentGetResponse:
        pass