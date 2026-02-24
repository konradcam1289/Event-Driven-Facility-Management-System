# Event-Driven Facility Management System

Monorepo projektu w strukturze multi-module Maven.

## Moduły

- **reservation-service** (REST API + event publisher)
- **notification-service** (event consumer)
- **reporting-service** (event consumer + agregacja)

## Budowanie

Z poziomu katalogu backend:

```bash
mvn clean install
```

## Run locally

### 1. Uruchomić infrastrukturę (RabbitMQ + PostgreSQL)

```bash
docker compose up -d
```

Sprawdzić health:
```bash
docker ps
```

RabbitMQ UI: http://localhost:15672 (guest/guest)

### 2. Build projektu

```bash
mvn clean install
```

### 3. Uruchomić 3 serwisy (w osobnych terminalach)

**Terminal 1 — Reservation Service (port 8081)**
```bash
mvn -q -pl services/reservation-service spring-boot:run -Dspring-boot.run.profiles=local
```

**Terminal 2 — Notification Service (port 8082)**
```bash
mvn -q -pl services/notification-service spring-boot:run -Dspring-boot.run.profiles=local
```

**Terminal 3 — Reporting Service (port 8083)**
```bash
mvn -q -pl services/reporting-service spring-boot:run -Dspring-boot.run.profiles=local
```

### 4. Test end-to-end flow

**Krok 1: Utworzenie rezerwacji**

```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "A-101",
    "startAt": "2026-02-25T10:00:00",
    "endAt": "2026-02-25T11:00:00",
    "title": "Daily standup",
    "createdBy": "konrad"
  }'
```

**Krok 2: Sprawdzić powiadomienie (port 8082)**

```bash
curl http://localhost:8082/api/notifications
```

**Krok 3: Sprawdzić raport (port 8083)**

```bash
curl http://localhost:8083/api/reports/reservations
```

### 5. Test health endpoints

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## Architecture

### Event Flow

```
POST /api/reservations (8081)
    ↓
Save to DB
    ↓
Publish: facility.events / reservation.created
    ↓
notification-service (8082) listens → Save Notification
reporting-service (8083) listens → Save ReservationReport
```

### Baza danych

Wszystkie serwisy: `edfms` (localhost:5432, edfms/edfms)

Tabele:
- `reservations` (reservation-service)
- `notifications` + `processed_events` (notification-service)
- `reservation_reports` + `processed_events` (reporting-service)

### Idempotencja

Każdy konsumer sprawdza `processed_events` przed przetworzeniem.
Jeśli event już przetworzony → pomijamy.

## Cleanup

```bash
docker compose down
docker compose down -v  # usuń volumes
```
