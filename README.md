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

## Deploy the frontend to Vercel

Import this repository into Vercel and set the project **Root Directory** to `frontend`.

Vite's default build settings are sufficient. Once the backend is public, add:

`VITE_API_BASE_URL=https://your-api-host.example`

Redeploy the frontend after adding or changing that environment variable.

## Deploy the API to Render

The root `render.yaml` defines the Spring Boot web service. Create a Render Blueprint from this repository, then provide these environment variables:

| Variable | Example / purpose |
| --- | --- |
| `DB_URL` | JDBC PostgreSQL URL, such as `jdbc:postgresql://host:5432/database` |
| `DB_USERNAME` | PostgreSQL user |
| `DB_PASSWORD` | PostgreSQL password |
| `CORS_ALLOWED_ORIGIN` | Exact Vercel origin, such as `https://your-project.vercel.app` |

Render builds from the `backend` directory with Maven and starts the packaged Spring Boot JAR. The health check is `/api/health`.

After Render is live:

1. Set Render's `CORS_ALLOWED_ORIGIN` to the production Vercel origin.
2. Set Vercel's `VITE_API_BASE_URL` to the Render service URL.
3. Redeploy both services when their corresponding environment variables change.
4. Verify `GET /api/health`, then test client creation, pet creation, booking, lookup, rescheduling, and cancellation end to end.

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
