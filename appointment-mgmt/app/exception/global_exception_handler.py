from fastapi import Request
from fastapi.responses import JSONResponse

from app.exception.exceptions import ApplicationBaseException, AppointmentNotFoundError

async def application_exception_handler(request: Request, exc: ApplicationBaseException):
    """Handle all application-specific exceptions."""
    status_code = 404 if isinstance(exc, AppointmentNotFoundError) else 500
    return JSONResponse(
        status_code=status_code,
        content={"status": status_code, "message": str(exc), "data": None}
    )

async def generic_exception_handler(request: Request, exc: Exception):
    """Catch-all for unexpected exceptions."""
    return JSONResponse(
        status_code=500,
        content={"status": 500, "message": "An unexpected error occurred", "data": None}
    )