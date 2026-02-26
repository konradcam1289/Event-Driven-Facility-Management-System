# COMMIT 3: Bean Validation + Global Exception Handler + Flyway Migrations

## 🎯 Cel
Dodać production-ready walidację, centralizowaną obsługę błędów i kontrolowane migracje bazy danych.

## ✅ Co zostało zrobione

### 1. Bean Validation (Jakarta Validation API)

#### Dependencies
- ✅ `spring-boot-starter-validation` w pom.xml (reservation, notification, reporting services)
- ✅ `flyway-core` + `flyway-database-postgresql` w pom.xml

#### CreateReservationRequest DTO
- ✅ `@NotBlank` na `roomId`, `title`, `createdBy`
- ✅ `@NotNull` na `startAt`, `endAt`
- ✅ `@FutureOrPresent` na `startAt`
- ✅ `@Future` na `endAt`
- ✅ Custom message dla każdej validacji

#### ReservationController
- ✅ `@Valid` annotation na request body
- ✅ Usunięta ręczna validacja `isValidTimeRange()`
- ✅ Spring automatycznie zwraca HTTP 400 + field errors

### 2. Global Exception Handler (@ControllerAdvice)

#### GlobalExceptionHandler.java
- ✅ `@ControllerAdvice` dla centralized exception handling
- ✅ `handleValidationException()` → HTTP 400 + fieldErrors map
- ✅ `handleEntityNotFoundException()` → HTTP 404
- ✅ `handleGlobalException()` → HTTP 500

#### EntityNotFoundException.java
- ✅ Custom exception class
- ✅ Extends `RuntimeException`

#### ErrorResponse record
```java
public record ErrorResponse(
    int status,
    String message,
    Map<String, String> fieldErrors,
    Instant timestamp
)
```

### 3. ReservationStatus Enum

```java
public enum ReservationStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");
}
```

- ✅ Dodane do `Reservation` entity
- ✅ Pole `status` z `@Enumerated(EnumType.STRING)`
- ✅ Getter i setter

### 4. Reservation Entity Enhancement

- ✅ `@Column(nullable = false)` `private ReservationStatus status = PENDING`
- ✅ Inicjalizacja w konstruktorze
- ✅ Getter `getStatus()` i setter `setStatus()`

### 5. ReservationResponse DTO

- ✅ Dodane pole `status: ReservationStatus`
- ✅ Zaktualizowana factory metoda `from()`

### 6. ReservationService

- ✅ `getReservationById(UUID id): Reservation`
- ✅ Implementacja z `EntityNotFoundException`

### 7. ReservationController - New Endpoint

**GET /api/reservations/{id}**
- ✅ Path parameter UUID
- ✅ Returns HTTP 200 + ReservationResponse
- ✅ Returns HTTP 404 if not found
- ✅ Integrated with exception handler

### 8. Flyway Database Migrations

#### Configuration
- ✅ `spring.jpa.hibernate.ddl-auto: validate` (production)
- ✅ `spring.jpa.hibernate.ddl-auto: update` (local profile)
- ✅ `spring.flyway.baselineOnMigrate: true`
- ✅ Migration directory: `src/main/resources/db/migration/`

#### V001__initial_schema.sql Files

**Reservation Service**
```sql
CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    room_id VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);
```

**Notification Service**
```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    recipient_email VARCHAR(255),
    message TEXT,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP
);

CREATE TABLE processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500)
);
```

**Reporting Service**
```sql
CREATE TABLE reservation_reports (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL UNIQUE,
    room_id VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    report_created_at TIMESTAMP NOT NULL
);

CREATE TABLE processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500)
);
```

### 9. Configuration Updates

#### application.yml (all services)
- ✅ `jpa.hibernate.ddl-auto: validate`
- ✅ `jpa.show-sql: false`
- ✅ `flyway.baselineOnMigrate: true`
- ✅ `logging.level.org.hibernate.SQL: WARN`

#### application-local.yml (reservation)
- ✅ `jpa.hibernate.ddl-auto: update`

## 📊 Files Modified/Created

### New Files
```
config/
  ├── GlobalExceptionHandler.java
  └── EntityNotFoundException.java
domain/
  └── ReservationStatus.java
src/main/resources/db/migration/
  └── V001__initial_schema.sql
```

### Modified Files
```
pom.xml (3 services) - dependencies
application.yml (3 services) - flyway config
application-local.yml - ddl-auto: update
ReservationController.java - @Valid, GET /{id}
CreateReservationRequest.java - @NotBlank, @Future validations
ReservationResponse.java - +status field
Reservation.java - +status field, ReservationStatus
ReservationService.java - getReservationById()
```

## 🧪 Validation Examples

### ✅ Valid Request
```json
{
  "roomId": "ROOM-101",
  "startAt": "2026-02-28T10:00:00",
  "endAt": "2026-02-28T11:00:00",
  "title": "Team meeting",
  "createdBy": "john.doe"
}
// HTTP 201 Created + ReservationResponse
```

### ❌ Invalid Request
```json
{
  "roomId": "",
  "startAt": null,
  "endAt": "2020-01-01",
  "title": "",
  "createdBy": ""
}
// HTTP 400 Bad Request
{
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": {
    "roomId": "roomId cannot be blank",
    "startAt": "startAt cannot be null",
    "endAt": "endAt must be in future",
    "title": "title cannot be blank",
    "createdBy": "createdBy cannot be blank"
  },
  "timestamp": "2026-02-26T23:50:00Z"
}
```

## 🎯 API Endpoints (Updated)

| Method | Endpoint | Status | Notes |
|--------|----------|--------|-------|
| POST | /api/reservations | 201/400 | Bean Validation |
| GET | /api/reservations/{id} | 200/404 | NEW |
| GET | /api/test | 200 | Health check |
| GET | /api/notifications | 200 | notification-service |
| GET | /api/reports/reservations | 200 | reporting-service |

## 🏗️ Architecture Improvements

### Before (Checkpoint 3)
- ❌ Ręczna validacja w controller
- ❌ Try-catch bloki rozrzucone po kodzie
- ❌ Hibernate auto-creates schema (niebezpieczne)
- ❌ Brak statusu rezerwacji

### After (Checkpoint 4)
- ✅ Deklaratywna Bean Validation
- ✅ Centralized exception handling
- ✅ Flyway controlled migrations
- ✅ ReservationStatus enum
- ✅ Type-safe error responses

## 💡 Key Concepts Demonstrated

1. **Bean Validation API**
   - Jakarta validation framework
   - Deklaratywne anotacje (@NotBlank, @Future itp.)
   - Custom messages

2. **Exception Handling**
   - @ControllerAdvice pattern
   - Structured error responses
   - Proper HTTP status codes (400, 404, 500)

3. **Database Migrations**
   - Flyway versioning
   - Schema evolution
   - Baseline on migrate

4. **REST API Design**
   - Proper response formats
   - Consistent error handling
   - Resource endpoints (GET by ID)

## 🚀 Build & Test

```bash
# Build
mvn clean install -DskipTests

# Run reservation-service (local profile)
mvn -pl services/reservation-service spring-boot:run \
    -Dspring-boot.run.profiles=local

# Test endpoints
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{...}'

curl http://localhost:8081/api/reservations/{id}
```

## ✨ Benefits

### For Users
- Clear error messages with field-level details
- Proper HTTP status codes
- Predictable API behavior

### For Developers
- No manual validation code
- Centralized error handling
- Database schema is versioned and reproducible

### For Operations
- Database migrations are tracked
- Schema changes are auditable
- Easy rollback with Flyway

## 📈 Portfolio Value

- ✅ Production-grade validation
- ✅ Enterprise exception handling pattern
- ✅ Database migration best practices
- ✅ RESTful API design patterns
- ✅ Type-safe enumerations

---

**Commit Message**: 
```
feat: add bean validation, exception handler, and flyway migrations

- Add Jakarta Bean Validation API with @NotBlank, @Future, @NotNull
- Implement GlobalExceptionHandler for centralized error handling
- Add Flyway database migrations for all 3 services
- Add ReservationStatus enum and status field to entity
- Add GET /api/reservations/{id} endpoint
- Configure ddl-auto: validate in production, update in local
- Update application.yml with Flyway configuration
```

**Co-related Checkpoints**:
- Checkpoint 1: Project structure
- Checkpoint 2: Docker & environment config
- Checkpoint 3: Event consumers
- **Checkpoint 4**: ✅ Validation & migrations
- Checkpoint 5: API enhancements (conflict detection, pagination)

