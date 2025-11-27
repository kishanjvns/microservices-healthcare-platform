from fastapi import FastAPI
from app.controller.appointment_controller import router
from app.config.database import init_db
from app.exception.exceptions import ApplicationBaseException
from app.exception.global_exception_handler import (
    application_exception_handler,
    generic_exception_handler
)

app = FastAPI(title="Appointment Management")

# Register exception handlers
app.add_exception_handler(ApplicationBaseException, application_exception_handler)
app.add_exception_handler(Exception, generic_exception_handler)

# Include routers
app.include_router(router, prefix="/api/v1")

@app.on_event("startup")
async def startup():
    await init_db()