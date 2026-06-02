# Appointment Management Service

A FastAPI-based microservice for managing appointments.

## Prerequisites

- Python 3.12+

## Setup

### 1. Create Virtual Environment

```bash
cd appointment-mgmt
py -m venv .venv
```

### 2. Activate Virtual Environment

**PowerShell:**
```powershell
.\.venv\Scripts\Activate.ps1
```

**Command Prompt:**
```cmd
.\.venv\Scripts\activate.bat
```

**Linux/macOS:**
```bash
source .venv/bin/activate
```

### 3. Install Dependencies

```bash
pip install -r requirement.txt
```

## Running the Application

```bash
uvicorn app.main:app --reload --port 8082
```

The API will be available at: http://localhost:8082

## API Documentation

Once running, access the interactive API docs at:
- Swagger UI: http://localhost:8082/docs
- ReDoc: http://localhost:8082/redoc

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/appointments/` | Create a new appointment |
| GET | `/api/v1/appointments/{id}` | Get appointment by ID |
