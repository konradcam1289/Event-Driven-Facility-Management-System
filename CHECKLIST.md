# EDFMS Checklist

## MVP

- [x] Repo i struktura spojne z multi-module Maven (✅ Checkpoint 1)
- [x] docker-compose.yml (RabbitMQ + Postgres) (✅ Checkpoint 2)
- [x] Konfiguracja przez ENV w 3 serwisach (✅ Checkpoint 2)
- [x] Kontrakt eventow w module shared (event envelope + wersja) (✅ Checkpoint 2)
- [x] End-to-end flow: create reservation -> DB -> event -> consumers (✅ Checkpoint 3)
- [x] Idempotencja konsumentow (processed_events) (✅ Commit 2)
- [x] Podstawowe testy integracyjne (✅ Commit 2)
- [x] README: Run locally + curl (✅ Checkpoint 2)
- [x] Bean Validation + Global Exception Handler (✅ Checkpoint 4)
- [x] Flyway Database Migrations (✅ Checkpoint 4)
- [x] ReservationStatus enum (✅ Checkpoint 4)
- [x] GET /api/reservations/{id} endpoint (✅ Checkpoint 4)

## Nice-to-have

- [ ] Retry/DLQ dla kolejek RabbitMQ
- [ ] Outbox w reservation-service
- [ ] Swagger/OpenAPI
- [ ] GitHub Actions CI
- [ ] Reservation conflict detection
- [ ] Pagination na GET endpoints

## Stretch goals

- [ ] Observability (Micrometer/OTel)
- [ ] Security (API key lub JWT)
- [ ] Dockerfiles per service
- [ ] Diagramy architektury

