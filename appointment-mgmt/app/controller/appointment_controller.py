from app.models.request_response_models import AppointmentCreateRequest
from app.models.request_response_models import ApplicationBaseResponse
from app.service.appointment_service_definition import AppointmentService
from app.service.appointment_service_impl import AppointmentServiceImpl

from fastapi import APIRouter, Depends, status
from app.config.database import get_db_connection
from app.config.settings import get_settings
import httpx

member_service_url = get_settings().EXTERNAL_SERVICE_MEMBER_URL


router = APIRouter(prefix="/appointments", tags=["Appointments"])

@router.post("/", response_model=ApplicationBaseResponse, status_code=status.HTTP_201_CREATED)
async def create_appointment(appointment_request: AppointmentCreateRequest, conn = Depends(get_db_connection)):
    appointment_service: AppointmentService = AppointmentServiceImpl(conn)
    # call the member service to validate doctor id and patient id
    complete_url_doctor = f"{member_service_url}/doctor/{appointment_request.doctor_id}"
    print(f"Calling member service at URL: {complete_url_doctor}")
    
    doctor_response = httpx.get(complete_url_doctor)
    print(f"Response from member service: {doctor_response.status_code} - {doctor_response.text}")
    if not doctor_response.status_code == 200:
        return ApplicationBaseResponse(
            status=404,
            message="Invalid doctor ID",
            data=None
        )
    doctor_data = doctor_response.json();
    print(f"Doctor Id: {doctor_data.get('data').get('id')} Name: {doctor_data.get('data').get('name')} received from member service")
    
    patient_response = httpx.get(f"{member_service_url}/patient/{appointment_request.patient_id}")
    if not patient_response.status_code == 200:
        return ApplicationBaseResponse(
            status=404,
            message="Invalid patient ID",
            data=None
        )
    patient_data = patient_response.json();
    print(f"Patient Id: {patient_data.get('data').get('id')} Name: {patient_data.get('data').get('name')} received from member service")
    
    response = await appointment_service.save_appointment(appointment_request)
    return response

@router.get("/{appointment_id}", response_model= ApplicationBaseResponse, status_code=status.HTTP_200_OK)
async def get_appointment(appointment_id: int, conn = Depends(get_db_connection)):
    appoappointment_service: AppointmentService = AppointmentServiceImpl(conn)
    response = await appoappointment_service.fetch_appointment_id(appointment_id)
    return response
