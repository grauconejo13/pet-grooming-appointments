# Pet Grooming Appointments

A booking system for a small pet-grooming business. The Java/Spring Boot API manages clients, pets, groomers, services, and appointments, with a React/Vite client for the booking experience.

## Stack

- Java 17
- Spring Boot
- PostgreSQL
- Spring JDBC + Flyway
- React + Vite
- Docker Compose for local PostgreSQL

## Local setup

### API

1. Start PostgreSQL: `docker compose up -d db`
2. Run the API: `cd backend && mvn spring-boot:run`
3. Check the service: `GET http://localhost:8080/api/health`

### Frontend

1. `cd frontend`
2. `npm install`
3. Copy `.env.example` to `.env` if you want live API data.
4. `npm run dev`

Without `VITE_API_BASE_URL`, the UI uses preview data so Vercel deployments remain visually testable before the API is publicly hosted.

## Vercel preview

Import this repository into Vercel and set the project **Root Directory** to `frontend`. Vite's default build settings are sufficient.

When the backend is deployed, add:

`VITE_API_BASE_URL=https://your-api-host.example`

## Core domain

| Record | Purpose |
| --- | --- |
| Client | A pet owner's contact information |
| Pet | A pet belonging to one client |
| Groomer | A member of the grooming team |
| Grooming service | A bookable service, duration, and price |
| Appointment | A scheduled pet, groomer, service, and time |

## First delivery scope

- Admin can manage clients, pets, groomers, and services.
- A client can book, view, reschedule, and cancel an appointment.
- The booking service prevents overlapping active appointments for the same groomer.
- Admin can view appointments by groomer and time window.
