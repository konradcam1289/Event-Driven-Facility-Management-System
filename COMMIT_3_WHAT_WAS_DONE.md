# Commit 3: Bean Validation, Exception Handler, Flyway Migrations

## Co zostało zrobione

### 1. Bean Validation
- Dodano `spring-boot-starter-validation` do 3 serwisów
- Adnotacje validacji w `CreateReservationRequest`:
  - `@NotBlank` na `roomId`, `title`, `createdBy`
  - `@NotNull` na `startAt`, `endAt`
  - `@FutureOrPresent` na `startAt`
  - `@Future` na `endAt`
- `@Valid` w `ReservationController`
- Spring automatycznie zwraca HTTP 400 z field errors

### 2. Global Exception Handler
- Stworzono `GlobalExceptionHandler.java` z `@ControllerAdvice`
- Stworzono `EntityNotFoundException.java` custom exception
- Handlers dla 3 scenariuszy:
  - Validation errors → HTTP 400
  - Entity not found → HTTP 404
  - Server errors → HTTP 500
- `ErrorResponse` record ze strukturą

### 3. Flyway Database Migrations
- Dodano `flyway-core` + `flyway-database-postgresql` do 3 serwisów
- Stworzono `V001__initial_schema.sql` dla:
  - reservation-service: tabela reservations
  - notification-service: notifications + processed_events
  - reporting-service: reservation_reports + processed_events
- Konfiguracja:
  - Production: `ddl-auto: validate`
  - Local: `ddl-auto: update`
  - `baselineOnMigrate: true`

### 4. Entity & API Enhancements
- Stworzono `ReservationStatus` enum (PENDING, CONFIRMED, CANCELLED)
- Dodano pole `status` w `Reservation` entity
- Implementowano `GET /api/reservations/{id}` endpoint
- Dodano `getReservationById()` w `ReservationService`
- Zaktualizowano `ReservationResponse` DTO

## Nowe pliki
- `config/GlobalExceptionHandler.java`
- `config/EntityNotFoundException.java`
- `domain/ReservationStatus.java`
- `src/main/resources/db/migration/V001__initial_schema.sql` (x3)

## Zmodyfikowane pliki
- `pom.xml` (3 serwisy)
- `application.yml` (3 serwisy)
- `application-local.yml`
- `ReservationController.java`
- `CreateReservationRequest.java`
- `ReservationResponse.java`
- `Reservation.java`
- `ReservationService.java`

## Rezultat
System jest teraz production-ready z:
- Walidacją danych na poziomie API
- Konsystentnym formatem błędów
- Kontrolowanymi migracjami bazy danych
- Type-safe statusami (enum)

