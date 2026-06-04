# Hotel Guest Service System — Backend

REST API for a hotel guest service platform. Guests can report issues (technical or reception-related), attach photos, and choose a preferred resolution time. Hotel staff can view and manage all submitted issues. Notifications are sent via email and SMS.

## Tech Stack

- **Java 21** with Spring Boot 4.0.5
- **PostgreSQL** — primary database
- **Spring Security** + **JWT** — authentication & authorization
- **Spring Mail** + **Mailpit** — email notifications (dev)
- **Twilio** — SMS notifications
- **Lombok**, **JPA/Hibernate**, **Bean Validation**

## Project Structure

```
src/main/java/com/jerzymaj/hotel_guest_service_system/
├── controllers/        # AuthController, IssueController
├── services/           # Business logic + notification senders
├── models/             # User, Issue
├── DTOs/               # Request/Response objects
├── repositories/       # JPA repositories
├── configuration/      # Security, JWT, CORS, Twilio config
├── enums/              # UserType, IssueType, IssueStatus, PreferredTimeOption
├── exceptions/         # Custom exceptions + global handler
├── security/           # AuthenticationFacade
└── translator/         # Entity ↔ DTO mapping
```

## API Endpoints

All endpoints are prefixed with `/hgss/api`.

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/auth/register` | Public | Register a new user |
| POST | `/auth/login` | Public | Login, returns JWT token |

### Issues

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/issues` | Authenticated | Create an issue (multipart/form-data, photo optional) |
| GET | `/issues/user-prof` | GUEST | Get all issues for the authenticated user |
| GET | `/issues/tech-prof` | TECHNICAL_SUPPORT / RECEPTIONIST | Get all issues |
| GET | `/issues/photos/{fileName}` | Authenticated | Fetch issue photo |
| PATCH | `/issues/{issueId}/status` | Staff | Update issue status |

### User Roles

| Role | Description |
|------|-------------|
| `GUEST` | Hotel guest — can submit and view own issues |
| `RECEPTIONIST` | Can view all issues of type `RECEPTION` |
| `TECHNICAL_SUPPORT` | Can view all issues of type `TECHNICAL` |

### Issue Types & Statuses

**Types:** `TECHNICAL`, `RECEPTION`

**Statuses:** `NEW` → `OPEN` → `CLOSED`

**Preferred time options:** `AS_SOON_AS_POSSIBLE`, `WHEN_NOT_IN_ROOM`, `NO_URGENCY`

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (for PostgreSQL and Mailpit)

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts PostgreSQL (port `5432`) and Mailpit (SMTP `1026`, UI `8026`).

### 2. Configure environment

Create a `fragile.env` file (or set environment variables):

```env
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
TWILIO_ACCOUNT_SID=your_sid
TWILIO_AUTH_TOKEN=your_token
TWILIO_PHONE_NUMBER=+1xxxxxxxxxx
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` with the `dev` profile by default.

### Mailpit UI

View sent emails at `http://localhost:8026`.

## Configuration

Key properties in `application.yaml`:

| Property | Env Variable | Default |
|----------|-------------|---------|
| JWT secret | `JWT_SECRET` | — |
| JWT expiry | — | 7 days |
| Allowed CORS origin | `ALLOWED_ORIGIN` | `http://localhost:5173` |
| Support email | `SUPPORT_EMAIL` | `tech@hotel.com` |
| Upload directory | `storage.upload-dir` | `/app/upload-dir` |
| Max file size | — | 10MB |

## Tests

```bash
./mvnw test
```

Includes unit tests (`IssueServiceTest`, `UserServiceTest`) and integration tests (`AuthControllerTest`, `IssueControllerTest`).
