# CHECKPOINT 4: Bean Validation + Global Exception Handler + Flyway Migrations

## ✅ Zrealizowane

### 1. Bean Validation (Jakarta Validation API)

#### Dependencies
- ✅ Dodano `spring-boot-starter-validation` do wszystkich 3 serwisów
- ✅ Flyway (`flyway-core`, `flyway-database-postgresql`) do wszystkich 3 serwisów

#### CreateReservationRequest DTO - Enhanced Validation
```java
public record CreateReservationRequest(
    @NotBlank(message = "roomId cannot be blank")
    String roomId,
    
    @NotNull(message = "startAt cannot be null")
    @FutureOrPresent(message = "startAt must be in future or present")
    LocalDateTime startAt,
    
    @NotNull(message = "endAt cannot be null")
    @Future(message = "endAt must be in future")
    LocalDateTime endAt,
    
    @NotBlank(message = "title cannot be blank")
    String title,
    
    @NotBlank(message = "createdBy cannot be blank")
    String createdBy
)
```

#### ReservationController - Validation Binding
- ✅ Dodano `@Valid` annotation na `@RequestBody` parametrze
- ✅ Usunięto ręczną validację `isValidTimeRange()` - Bean Validation się tym zajmuje
- ✅ Spring automatycznie zwraca HTTP 400 z pełnym opisem błędów

### 2. Global Exception Handler (@ControllerAdvice)

#### Stworzono 2 nowe klasy:

**GlobalExceptionHandler.java**
- ✅ `@ControllerAdvice` - scentralizowana obsługa wyjątków
- ✅ `MethodArgumentNotValidException` handler → HTTP 400
  - Ekstrahuje `fieldErrors` z błędu validacji
  - Zwraca mapę: field → message
- ✅ `EntityNotFoundException` handler → HTTP 404
- ✅ Generic `Exception` handler → HTTP 500

**ErrorResponse record**
```java
public record ErrorResponse(
    int status,
    String message,
    Map<String, String> fieldErrors,
    Instant timestamp
)
```

**EntityNotFoundException.java**
- ✅ Custom exception dla entity not found
- ✅ Używana w `ReservationService.getReservationById()`

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
- ✅ Domyślna wartość: `PENDING`
- ✅ Getter i setter dla zmiany statusu

### 4. Reservation Entity - Enhancement

- ✅ Dodano pole `status: ReservationStatus`
- ✅ Dodano getter i setter dla `status`
- ✅ Inicjalizacja w konstruktorze: `status = PENDING`

### 5. ReservationResponse DTO - Enhancement

- ✅ Dodano pole `status: ReservationStatus`
- ✅ Zaktualizowano factory method `from()`

### 6. ReservationService - Enhancement

- ✅ Dodano metodę `getReservationById(UUID id): Reservation`
- ✅ Implementacja z `EntityNotFoundException`
- ✅ Importy: `EntityNotFoundException`, `UUID`

### 7. ReservationController - New Endpoints

**GET /api/reservations/{id}**
```
Request: UUID id (path parameter)
Response: HTTP 200 + ReservationResponse
Errors:
  - HTTP 404 jeśli rezerwacja nie istnieje
  - HTTP 500 jeśli błąd serwera
```

**POST /api/reservations** (updated)
- ✅ Bean Validation na wszystkich polach
- ✅ HTTP 400 + field errors jeśli validacja nie przejdzie
- ✅ HTTP 201 Created + ReservationResponse jeśli ok

### 8. Flyway Database Migrations

#### Configuration
- ✅ `spring.jpa.hibernate.ddl-auto: validate` (production mode)
- ✅ `spring.flyway.baselineOnMigrate: true` (dla nowych baz)
- ✅ Migration files w: `src/main/resources/db/migration/`

#### V001__initial_schema.sql (reservation-service)
```sql
CREATE TABLE IF NOT EXISTS reservations (
    id UUID PRIMARY KEY,
    room_id VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX idx_reservations_room_id ON reservations(room_id);
CREATE INDEX idx_reservations_start_at ON reservations(start_at);
CREATE INDEX idx_reservations_created_at ON reservations(created_at);
```

#### V001__initial_schema.sql (notification-service)
```sql
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    recipient_email VARCHAR(255),
    message TEXT,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500)
);

CREATE INDEX idx_notifications_reservation_id ON notifications(reservation_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_processed_events_event_id ON processed_events(event_id);
CREATE INDEX idx_processed_events_event_type ON processed_events(event_type);
```

#### V001__initial_schema.sql (reporting-service)
```sql
CREATE TABLE IF NOT EXISTS reservation_reports (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL UNIQUE,
    room_id VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    report_created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500)
);

CREATE INDEX idx_reservation_reports_reservation_id ON reservation_reports(reservation_id);
CREATE INDEX idx_reservation_reports_room_id ON reservation_reports(room_id);
CREATE INDEX idx_processed_events_event_id ON processed_events(event_id);
CREATE INDEX idx_processed_events_event_type ON processed_events(event_type);
```

### 9. Configuration Updates

#### application.yml (wszystkie 3 serwisy)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway zarządza schematem
    show-sql: false
  flyway:
    baselineOnMigrate: true  # Auto-initialize empty DB

logging:
  level:
    org.hibernate.SQL: WARN  # Zmniejszona verbosity
```

#### application-local.yml (reservation-service)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Dev mode - auto-update
```

## 🎯 End-to-End Flow (Checkpoint 4)

### Success Path - Tworzenie rezerwacji
```
1. POST /api/reservations
   {
     "roomId": "ROOM-101",
     "startAt": "2026-02-28T10:00:00",
     "endAt": "2026-02-28T11:00:00",
     "title": "Team meeting",
     "createdBy": "john.doe"
   }
   ↓
2. Bean Validation
   - roomId: ✅ not blank
   - startAt: ✅ future or present
   - endAt: ✅ future
   - title: ✅ not blank
   - createdBy: ✅ not blank
   ↓
3. ReservationService.createReservation()
   - Stwórz Reservation(status=PENDING)
   - Zapisz do DB (Flyway schema)
   - Publish event
   ↓
4. HTTP 201 Created
   {
     "id": "12345",
     "roomId": "ROOM-101",
     "startAt": "2026-02-28T10:00:00",
     "endAt": "2026-02-28T11:00:00",
     "title": "Team meeting",
     "createdBy": "john.doe",
     "status": "PENDING",
     "createdAt": "2026-02-26T23:50:00Z"
   }
```

### Validation Error Path
```
POST /api/reservations
{
  "roomId": "",           // ❌ blank
  "startAt": null,        // ❌ null
  "endAt": "2026-01-01",  // ❌ past
  "title": "",            // ❌ blank
  "createdBy": ""         // ❌ blank
}
↓
Bean Validation fails → Spring catches MethodArgumentNotValidException
↓
GlobalExceptionHandler.handleValidationException() triggers
↓
HTTP 400 Bad Request
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

### Entity Not Found Path
```
GET /api/reservations/99999999-9999-9999-9999-999999999999
↓
ReservationService.getReservationById() throws EntityNotFoundException
↓
GlobalExceptionHandler.handleEntityNotFoundException() triggers
↓
HTTP 404 Not Found
{
  "status": 404,
  "message": "Reservation not found with id: 99999999-9999-9999-9999-999999999999",
  "fieldErrors": null,
  "timestamp": "2026-02-26T23:50:00Z"
}
```

## 📊 Zmeny w strukturze

### Nowe pliki
```
reservation-service/
├── src/main/java/pl/konradcam/reservation/
│   ├── config/
│   │   ├── GlobalExceptionHandler.java (NEW)
│   │   └── EntityNotFoundException.java (NEW)
│   ├── domain/
│   │   └── ReservationStatus.java (NEW)
│   └── ...
└── src/main/resources/db/migration/
    └── V001__initial_schema.sql (NEW)

notification-service/
└── src/main/resources/db/migration/
    └── V001__initial_schema.sql (NEW)

reporting-service/
└── src/main/resources/db/migration/
    └── V001__initial_schema.sql (NEW)
```

### Zmodyfikowane pliki
```
reservation-service/
├── pom.xml (+ validation, flyway deps)
├── src/main/resources/
│   ├── application.yml (+ flyway config, validate ddl-auto)
│   └── application-local.yml (ddl-auto: update)
└── src/main/java/pl/konradcam/reservation/
    ├── controller/
    │   ├── ReservationController.java (@Valid, GET /{id})
    │   └── dto/
    │       ├── CreateReservationRequest.java (+ @NotBlank, @Future)
    │       └── ReservationResponse.java (+ status field)
    ├── domain/
    │   └── Reservation.java (+ status field, ReservationStatus)
    └── service/
        └── ReservationService.java (getReservationById)

notification-service/
├── pom.xml (+ validation, flyway deps)
└── src/main/resources/
    └── application.yml (+ flyway config, validate ddl-auto)

reporting-service/
├── pom.xml (+ validation, flyway deps)
└── src/main/resources/
    └── application.yml (+ flyway config, validate ddl-auto)
```

## ✨ Korzyści dla systemu

### Production Readiness
✅ **Database Migrations**: Kontrolowana ewolucja schematu (Flyway)
✅ **Validation**: Bean Validation zamiast custom logic
✅ **Error Handling**: Centralized exception handling
✅ **Status Tracking**: Możliwość śledzenia statusu rezerwacji

### Code Quality
✅ **Declarative Validation**: Anotacje zamiast if statements
✅ **Separation of Concerns**: GlobalExceptionHandler → centralna logika
✅ **Type Safety**: Enum dla statusów
✅ **Maintainability**: Łatwo dodać nowe validacje

### Developer Experience
✅ **Clear Error Messages**: Użytkownik wie dokładnie co jest złe
✅ **Automatic Validation**: Spring obsługuje wszystko
✅ **Schema Evolution**: Flyway zarządza zmianami w schemacie
✅ **Rollback Safety**: Migracje są wersjonowane

## 🧪 Testowanie (manual)

### Test 1: Valid Request
```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "ROOM-101",
    "startAt": "2026-02-28T10:00:00",
    "endAt": "2026-02-28T11:00:00",
    "title": "Team meeting",
    "createdBy": "john.doe"
  }'

# Expected: HTTP 201 Created + ReservationResponse
```

### Test 2: Validation Error (blank fields)
```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "",
    "startAt": "2026-02-28T10:00:00",
    "endAt": "2026-02-28T11:00:00",
    "title": "",
    "createdBy": ""
  }'

# Expected: HTTP 400 + fieldErrors
```

### Test 3: Validation Error (invalid dates)
```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "ROOM-101",
    "startAt": "2020-01-01T10:00:00",
    "endAt": "2020-01-01T09:00:00",
    "title": "Meeting",
    "createdBy": "john.doe"
  }'

# Expected: HTTP 400 + fieldErrors
```

### Test 4: Get by ID (success)
```bash
curl http://localhost:8081/api/reservations/12345

# Expected: HTTP 200 + ReservationResponse
```

### Test 5: Get by ID (not found)
```bash
curl http://localhost:8081/api/reservations/99999999-9999-9999-9999-999999999999

# Expected: HTTP 404 + ErrorResponse
```

## 🚀 Build Status

```
[INFO] Building event-driven-facility 0.0.1-SNAPSHOT
[INFO] ----
[INFO] reservation-service ....... SUCCESS
[INFO] notification-service ...... SUCCESS
[INFO] reporting-service ......... SUCCESS
[INFO] ----
[INFO] BUILD SUCCESS
```

## 📝 Commits & Versions

- **Checkpoint 1**: Skeleton aplikacji
- **Checkpoint 2**: Event-driven flow + idempotencja
- **Checkpoint 3**: Listeners w consumer services
- **Checkpoint 4** (THIS): ✅ Bean Validation + Global Exception Handler + Flyway

---

## 🎓 Portfolio Value

### Skills Demonstrated
- ✅ **Input Validation**: Jakarta Bean Validation API
- ✅ **Exception Handling**: @ControllerAdvice pattern
- ✅ **Database Migrations**: Flyway best practices
- ✅ **REST API Design**: Proper HTTP status codes
- ✅ **Error Responses**: Structured error formats
- ✅ **Enum Usage**: Type-safe status tracking

### Interview Ready Answers
- ❓ "Jak obsługujesz błędy w REST API?"
  - ✅ Global exception handler + structured error responses

- ❓ "Jak zapewniasz spójność danych w bazie?"
  - ✅ Flyway migrations + schema versioning

- ❓ "Jak walidowałbyś dane wejściowe?"
  - ✅ Jakarta Bean Validation + custom constraints

---

## 🔄 Next Steps (Checkpoint 5)

Potential features:
- [ ] Reservation cancellation (PATCH /api/reservations/{id}/cancel)
- [ ] Conflict detection (overlapping reservations)
- [ ] Pagination for GET /api/reservations
- [ ] Filtering and sorting
- [ ] OpenAPI/Swagger documentation
- [ ] Integration tests with Testcontainers


