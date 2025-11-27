from enum import Enum

class appointment_status(str, Enum):
    SCHEDULED = "Scheduled"
    RESCHEDULED = "ReScheduled"
    INPROGRESS = "in_progress"
    COMPLETED = "Completed"
    CANCELED = "Canceled"
    NO_SHOW = "No-Show"