# CHECKPOINT 3 — INSTRUKCJA TESTOWANIA

## Przygotowanie środowiska

### 1. Ustaw Java 21

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

Powinno wyświetlić: `java version "21.0.2"`

### 2. Uruchom infrastrukturę

```bash
cd C:\Users\Konrad\projects\edfms\backend
docker compose up -d
```

Sprawdzenie:
```bash
docker ps
```

Powinno pokazać:
- `edfms-rabbitmq` (port 5672/15672)
- `edfms-postgres` (port 5432)

### 3. Build projektu

```bash
mvn clean install
```

Powinno skończyć się bez błędów.

---

## Uruchomienie serwisów

Otwórz **3 odrębne PowerShell terminale** i uruchom każdy serwis:

### Terminal 1 — Reservation Service (port 8081)

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd "C:\Users\Konrad\projects\edfms\backend"
mvn -q -pl services/reservation-service spring-boot:run -Dspring-boot.run.profiles=local
```

Czekaj na log: `Started ReservationServiceApplication in X seconds`

### Terminal 2 — Notification Service (port 8082)

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd "C:\Users\Konrad\projects\edfms\backend"
mvn -q -pl services/notification-service spring-boot:run -Dspring-boot.run.profiles=local
```

Czekaj na log: `Started NotificationServiceApplication in X seconds`

### Terminal 3 — Reporting Service (port 8083)

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd "C:\Users\Konrad\projects\edfms\backend"
mvn -q -pl services/reporting-service spring-boot:run -Dspring-boot.run.profiles=local
```

Czekaj na log: `Started ReportingServiceApplication in X seconds`

---

## Test Health Endpoints

W czwartym terminalu (lub w Postmanie/curl):

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

Powinno zwrócić: `{"status":"UP"}`

---

## TEST 1: Tworzenie rezerwacji

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

### Oczekiwany response (HTTP 201):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "roomId": "A-101",
  "startAt": "2026-02-25T10:00:00",
  "endAt": "2026-02-25T11:00:00",
  "title": "Daily standup",
  "createdBy": "konrad",
  "createdAt": "2026-02-24T12:34:56.789Z"
}
```

### Co się dzieje w tle:
1. Reservation zapisana do DB
2. Event opublikowany na RabbitMQ (facility.events / reservation.created)
3. notification-service odbiera event → tworzy Notification
4. reporting-service odbiera event → tworzy ReservationReport

---

## TEST 2: Sprawdzenie powiadomień (notification-service)

```bash
curl http://localhost:8082/api/notifications
```

### Oczekiwany response:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "reservationId": "550e8400-e29b-41d4-a716-446655440001",
    "status": "PENDING",
    "recipientEmail": "konrad@example.com",
    "message": "Rezerwacja: Daily standup w sali A-101 od 2026-02-25T10:00 do 2026-02-25T11:00",
    "createdAt": "2026-02-24T12:34:57Z",
    "sentAt": null
  }
]
```

### Walidacja:
- [ ] Status = `PENDING`
- [ ] recipientEmail = `konrad@example.com`
- [ ] message zawiera dane rezerwacji
- [ ] createdAt jest bliskie czasowi POST'u

---

## TEST 3: Sprawdzenie raportów (reporting-service)

```bash
curl http://localhost:8083/api/reports/reservations
```

### Oczekiwany response:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "reservationId": "550e8400-e29b-41d4-a716-446655440001",
    "roomId": "A-101",
    "startAt": "2026-02-25T10:00:00",
    "endAt": "2026-02-25T11:00:00",
    "title": "Daily standup",
    "createdBy": "konrad",
    "reportCreatedAt": "2026-02-24T12:34:57Z"
  }
]
```

### Walidacja:
- [ ] reservationId = to samo co w rejestracji
- [ ] roomId, startAt, endAt, title, createdBy = skopiowane z rezerwacji
- [ ] reportCreatedAt jest blisko czasowi POST'u

---

## TEST 4: Idempotencja

Wyślij ten sam POST request **dwa razy** i sprawdzić, czy:

```bash
# Pierwszy POST
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"roomId":"B-202","startAt":"2026-02-26T14:00:00","endAt":"2026-02-26T15:00:00","title":"Meeting","createdBy":"alice"}'

# Drugi POST (z innym ID, ale notification/report będzie duplikat eventId)
# Skopiuj eventId z logów notification-service
```

Sprawdzenie w notification-service:

```bash
curl http://localhost:8082/api/notifications | grep -o '"reservationId":"[^"]*"' | sort | uniq -c
```

Powinno być **2 unikalne reservationId**, ale jeśli powtórzysz event — powinna być idempotencja.

---

## TEST 5: Walidacja (Error Handling)

Przetestuj błędy:

### 5.1 startAt > endAt (HTTP 400)

```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"roomId":"C-303","startAt":"2026-02-27T15:00:00","endAt":"2026-02-27T14:00:00","title":"Invalid","createdBy":"bob"}'
```

Oczekiwany response (HTTP 400):
```json
{
  "message": "startAt must be before endAt"
}
```

### 5.2 Brakujące pole (HTTP 400)

```bash
curl -X POST http://localhost:8081/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"roomId":"D-404","startAt":"2026-02-28T10:00:00","endAt":"2026-02-28T11:00:00"}'
```

Powinno zwrócić HTTP 400.

---

## TEST 6: RabbitMQ UI

Otwórz: http://localhost:15672  
Zaloguj: `guest/guest`

W sekcji **Queues** powinny być:
- `reservation.created.notification`
- `reservation.created.reporting`

Obydwie powinny mieć **0 messages** (bo serwisy je przetwarzają).

W sekcji **Exchanges**:
- `facility.events` (TopicExchange)

---

## TEST 7: Baza danych

### Połączenie do PostgreSQL:

```powershell
# Zainstaluj psql jeśli nie masz:
# Albo użyj Docker:
docker exec -it edfms-postgres psql -U edfms -d edfms
```

### Sprawdzenie tabel:

```sql
-- Rezerwacje
SELECT id, room_id, title, created_by, created_at FROM reservations;

-- Powiadomienia
SELECT id, reservation_id, status, recipient_email FROM notifications;

-- Raporty
SELECT id, reservation_id, room_id, title FROM reservation_reports;

-- Przetworzenie eventów (idempotencja)
SELECT event_id, event_type, processed_at FROM processed_events;
```

---

## Troubleshooting

### Problem: "Port 8081 already in use"
```bash
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### Problem: "Failed to connect to PostgreSQL"
```bash
docker logs edfms-postgres
docker ps -a
```

### Problem: "Failed to connect to RabbitMQ"
```bash
docker logs edfms-rabbitmq
```

### Problem: Event nie dotarł do konsumerów
Sprawdź logi serwisów — powinny pokazać:
```
Received reservation.created event: eventId=..., reservationId=...
```

### Problem: Duplikat notification/report
Normalnie — jeśli wysłałeś dwa POST'y, będą dwa rekordy. To nie jest problem idempotencji.

---

## Checklist — Pomyślne Checkpoint 3:

- [ ] Wszystkie 3 serwisy uruchomione bez błędów
- [ ] POST /api/reservations zwraca 201
- [ ] Notification pojawia się w GET /api/notifications
- [ ] ReservationReport pojawia się w GET /api/reports/reservations
- [ ] Walidacja startAt < endAt działa (400 error)
- [ ] RabbitMQ UI pokazuje exchange + queues
- [ ] Baza danych ma rekordy w wszystkich tabelach
- [ ] Logi pokazują "Received reservation.created event"

---

## Cleanup

Kiedy skończysz testy:

```bash
# Zatrzymaj serwisy (Ctrl+C w każdym terminalu)

# Zatrzymaj infrastrukturę
docker compose down

# Usuń volumes (opcjonalnie)
docker compose down -v
```

---

**Status: Ready for Testing** ✅

Powodzenia!

