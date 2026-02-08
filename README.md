# Employee Management System with RabbitMQ (Spring Boot)

This project implements the **Employee Management System** technical assessment with:
- Spring Boot 2.7 (Java 11)
- PostgreSQL
- RabbitMQ (publish + consume notifications)
- Spring Security (Basic Auth)
- Docker & Docker Compose

## Quick Start (Docker)

```bash
docker compose up --build
```

- App: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- RabbitMQ UI: http://localhost:15672 (guest/guest)

## Auth Users (demo)
- **ADMIN**: `admin / admin123`
- **USER**: `user / user123`

> Note: For realistic USER behavior, create an Employee whose email is `user` (or change the USER username to match an employee email).

## API

### Departments (ADMIN only)
- `GET /api/departments`
- `POST /api/departments`
- `GET /api/departments/{id}/employees`

### Employees (ADMIN only except /me)
- `GET /api/employees?page=0&sort=name&departmentId=1`
- `POST /api/employees`
- `GET /api/employees/{id}` (USER can access only own)
- `GET /api/employees/me`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`

### Leaves
- `POST /api/leaves` (USER can apply only for own employeeId)
- `PUT /api/leaves/{id}/status` (ADMIN only)
- `GET /api/leaves/employee/{empId}` (USER only for self)

## RabbitMQ Notifications

### 1) Employee Created
Triggered when an Employee is created.

### 2) Leave Status Changed
Triggered when a Leave request status is updated.

Consumers log all received messages and simulate email sending.

## Run Locally (without Docker)
1. Start Postgres + RabbitMQ locally
2. Run:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Assumptions
- USER access is mapped to employee email (demo setup uses username `user`).
- Notification for leave is only sent on status update, per assessment.
