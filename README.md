# Healthcare Microservices Platform
### Phase 1
A cloud-native healthcare appointment management system built with microservices architecture, demonstrating service-to-service communication, containerization, and Kubernetes orchestration.

## 📋 Table of Contents
- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Microservices Communication](#microservices-communication)
- [Local Development Setup](#local-development-setup)
- [Kubernetes Deployment](#kubernetes-deployment)
- [API Documentation](#api-documentation)

---

## 🏗️ Architecture Overview

This platform consists of two independent microservices:

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                        │
│                                                              │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │  Member Management   │      │  Appointment Mgmt    │    │
│  │     Service          │◄─────┤     Service          │    │
│  │  (Spring Boot)       │      │   (FastAPI)          │    │
│  │  Port: 8080          │      │   Port: 8080         │    │
│  └──────────┬───────────┘      └──────────┬───────────┘    │
│             │                              │                │
│  ┌──────────▼───────────┐      ┌──────────▼───────────┐    │
│  │   H2 DB      │      │            SQLite DB         │    │
│  └──────────────────────┘      └──────────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Services:

#### 1. **Member Management Service**
- Manages doctors and patients
- Provides CRUD operations for member entities
- RESTful API endpoints for member validation

#### 2. **Appointment Management Service**
- Handles appointment booking and scheduling
- Validates doctors and patients via Member Service
- Manages appointment lifecycle (scheduled, completed, cancelled)

---

## 🛠️ Tech Stack

### Member Management Service
| Component | Technology |
|-----------|------------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.x |
| **Database** | H2, PostgreSQL |
| **Build Tool** | Maven |
| **ORM** | Spring Data JPA / Hibernate |
| **API Documentation** | Swagger/OpenAPI |

### Appointment Management Service
| Component | Technology |
|-----------|------------|
| **Language** | Python 3.12+ |
| **Framework** | FastAPI |
| **Database** | SQLite (dev), PostgreSQL (prod) |
| **Async HTTP Client** | httpx |
| **ORM** | SQLAlchemy (async) |
| **Data Validation** | Pydantic |
| **ASGI Server** | Uvicorn |

### Infrastructure & DevOps
| Component | Technology |
|-----------|------------|
| **Containerization** | Docker |
| **Orchestration** | Kubernetes |
| **Configuration Management** | ConfigMaps, .env files |
| **Service Discovery** | Kubernetes DNS |
| **Networking** | Kubernetes Services (NodePort/ClusterIP) |

---

## 🔄 Microservices Communication

### Communication Pattern: Synchronous REST API

The Appointment Service validates member entities by making HTTP requests to the Member Service:



### Key Communication Details:

**Service Discovery:**
- **Local Development**: Direct HTTP calls via `http://127.0.0.1:8080`
- **Kubernetes**: DNS-based discovery via `http://member-mgmt-service:80`

**Configuration Strategy:**
```python
# Local (.env file)
EXTERNAL_SERVICE_MEMBER_URL=http://127.0.0.1:8080

# Kubernetes (ConfigMap override)
EXTERNAL_SERVICE_MEMBER_URL=http://member-mgmt-service:80
```


##  Local Development Setup

### Prerequisites
- **Java**: JDK 17 or higher
- **Python**: 3.12 or higher
- **Maven**: 3.6+
- **PostgreSQL**: 14+ (for Member Service)
- **Git**: Latest version

### 1. Clone the Repository
```bash
git clone <repository-url>
cd health_care
```

### 2. Setup Member Management Service

```bash
# Navigate to member service
cd member-mgmt

# Configure database in src/main/resources/application.yaml
# Update PostgreSQL connection details

# Build and run
mvn clean install
mvn spring-boot:run

# Service will start on http://localhost:8080
```

**Verify Member Service:**
```bash
curl http://localhost:8080/api/v1/health
```

### 3. Setup Appointment Management Service

```bash
# Navigate to appointment service
cd appointment-mgmt

# Create virtual environment
python -m venv .venv

# Activate virtual environment
# Windows:
.venv\Scripts\activate
# Linux/Mac:
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Copy and configure environment file
cp .env.example .env

# Edit .env file with your settings
# Make sure EXTERNAL_SERVICE_MEMBER_URL=http://127.0.0.1:8080

# Initialize database
python -c "from app.config.database import init_db; import asyncio; asyncio.run(init_db())"

# Run the service
uvicorn app.main:app --reload --port 8082

# Service will start on http://localhost:8082
```


### 4. Access API Documentation

- **Member Service**: http://localhost:8080/swagger-ui.html
- **Appointment Service**: http://localhost:8082/docs

---

## ☸️ Kubernetes Deployment

### Prerequisites
- Kubernetes cluster (Minikube, Docker Desktop, or cloud provider)
- kubectl CLI installed and configured
- Docker images pushed to a registry (or available locally)

### 1. Build and Push Docker Images

#### Member Management Service
```bash
cd member-mgmt

# Build Docker image
docker build -t <your-dockerhub-username>/member-mgmt:latest .

# Push to registry
docker push <your-dockerhub-username>/member-mgmt:latest
```

#### Appointment Management Service
```bash
cd appointment-mgmt

# Build Docker image
docker build -t <your-dockerhub-username>/appointment-mgmt:latest .

# Push to registry
docker push <your-dockerhub-username>/appointment-mgmt:latest
```

### 2. Deploy to Kubernetes

#### Deploy Member Service
```bash
# Apply service
kubectl apply -f ci_cd/k8s/k8s_member_deployement/service.yaml

# Apply deployment
kubectl apply -f ci_cd/k8s/k8s_member_deployement/deployment.yaml

# Verify deployment
kubectl get pods -l app=member-mgmt
kubectl get svc member-mgmt-service
```

#### Deploy Appointment Service
```bash
# Apply ConfigMap (contains environment variables)
kubectl apply -f ci_cd/k8s/k8s_appoinment_deployment/configmap.yaml

# Apply service
kubectl apply -f ci_cd/k8s/k8s_appoinment_deployment/service.yaml

# Apply deployment
kubectl apply -f ci_cd/k8s/k8s_appoinment_deployment/deployment.yaml

# Verify deployment
kubectl get pods -l app=appointment-mgmt
kubectl get svc appointment-mgmt-service
kubectl get configmap appointment-mgmt-config
```

### 3. Verify Service Communication

```bash
# Check logs to verify configuration loaded
kubectl logs -l app=appointment-mgmt

# You should see:
# 🔧 Loaded settings from: .env
# 🌍 Environment: dev
# 🔗 Member Service URL: http://member-mgmt-service:80

# Port forward to test locally
kubectl port-forward svc/member-mgmt-service 8080:80
kubectl port-forward svc/appointment-mgmt-service 8082:80

# Test in another terminal
curl http://localhost:8080/api/v1/health
curl http://localhost:8082/api/v1/health
```

