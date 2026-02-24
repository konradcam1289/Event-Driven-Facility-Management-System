# EDFMS Checklist

## MVP

- [ ] Repo i struktura spojne z multi-module Maven
- [ ] docker-compose.yml (RabbitMQ + Postgres)
- [ ] Konfiguracja przez ENV w 3 serwisach
- [ ] Kontrakt eventow w module shared (event envelope + wersja)
- [ ] End-to-end flow: create reservation -> DB -> event -> consumers
- [ ] Idempotencja konsumentow (processed_events)
- [ ] Podstawowe testy integracyjne
- [ ] README: Run locally + curl

## Nice-to-have

- [ ] Retry/DLQ dla kolejek RabbitMQ
- [ ] Outbox w reservation-service
- [ ] Flyway/Liquibase
- [ ] Swagger/OpenAPI
- [ ] GitHub Actions CI

## Stretch goals

- [ ] Observability (Micrometer/OTel)
- [ ] Security (API key lub JWT)
- [ ] Dockerfiles per service
- [ ] Diagramy architektury

