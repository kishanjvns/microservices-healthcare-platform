from app.service.appointment_service_definition import AppointmentService
from app.models.request_response_models import AppointmentCreateRequest
from app.models.request_response_models import ApplicationBaseResponse
from app.models.request_response_models import AppointmentGetResponse
from app.repositories.appointment_repository import appoinment_repository
from app.utils.mapper import ModelMapperStrategy, DefaultModelMapperStrategy
from app.constants.appointment_status import appointment_status
from app.exception.exceptions import AppointmentNotFoundError

from sqlalchemy.ext.asyncio import AsyncConnection

class AppointmentServiceImpl(AppointmentService) :
    def __init__(self, conn: AsyncConnection):
        self.appointment_repository = appoinment_repository(conn)
        self.mapper: ModelMapperStrategy = DefaultModelMapperStrategy()

    async def save_appointment(self, appointment_request: AppointmentCreateRequest) -> ApplicationBaseResponse:
        # Convert Pydantic model to dictionary
            appointment_dict = self.mapper.convert(appointment_request,dict)
            # Ensure patient_id and doctor_id UUIDs are converted to strings, as sqlalchemy not support UUID type directly
            appointment_dict['patient_id'] = str(appointment_dict['patient_id'])
            appointment_dict['doctor_id'] = str(appointment_dict['doctor_id'])
            appointment_dict['status'] = appointment_status.SCHEDULED.value
            # save appointment using repository
            create_appointment_id = await self.appointment_repository.create_appointment(appointment_dict)
            return ApplicationBaseResponse(status=201, message="Appointment created Successfully", data={"appointment_id": create_appointment_id})


    async def fetch_appointment_id(self, appointment_id: int) -> AppointmentGetResponse:
        fetched_appointment = await self.appointment_repository.fetch_appointment_id(appointment_id)
        if fetched_appointment is not None:
            appointment_get_response:AppointmentGetResponse = self.mapper.convert(fetched_appointment, AppointmentGetResponse)
            return ApplicationBaseResponse(status=200, message="Successfull", data=appointment_get_response.model_dump())
        else:
            raise AppointmentNotFoundError(f"Appointment with id {appointment_id} not found")