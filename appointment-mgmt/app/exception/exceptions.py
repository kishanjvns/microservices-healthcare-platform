
class ApplicationBaseException(Exception):
    """Base exception for the appointment management application."""
    pass


class AppointmentNotFoundError(ApplicationBaseException):
    """Exception raised when an appointment is not found."""
    pass