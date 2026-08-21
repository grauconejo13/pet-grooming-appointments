# Pet Grooming Appointments

A booking system for a small pet-grooming business. The first milestone is a Java API that manages clients, pets, groomers, services, and appointments.

## Stack

- Java 17
- Spring Boot
- PostgreSQL
- Spring Data JPA + Flyway
- Docker Compose for local PostgreSQL

React will be added later as a separate client after the appointment API is working.

## Core domain

| Record | Purpose |
| --- | --- |
| Client | A pet owner's contact information |
| Pet | A pet belonging to one client |
| Groomer | A member of the grooming team |
| Grooming service | A bookable service, duration, and price |
| Appointment | A scheduled pet, groomer, service, and time |

## Local setup

1. Start PostgreSQL: `docker compose up -d db`
2. Install Maven if it is not already available.
3. Run the API: `cd backend && mvn spring-boot:run`
4. Check the service: `GET http://localhost:8080/api/health`

The database schema is created by Flyway at application startup.

## First delivery scope

- Admin can manage clients, pets, groomers, and services.
- A client can book, view, reschedule, and cancel an appointment.
- The booking service prevents two active appointments for the same groomer at the same start time.
- Admin can view today's appointments.

Payments, authentication, public hosting, and the React client are intentionally out of scope for the first milestone.

## Current API

The first resource is grooming services. With the API running locally:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/services` | Create a bookable grooming service |
| `GET` | `/api/services` | List services |
| `GET` | `/api/services/{id}` | View one service |

Example request for `POST /api/services`:

```json
{
  "name": "Full Groom",
  "description": "Bath, brush, haircut, and nail trim.",
  "durationMinutes": 90,
  "price": 65.00
}
```
