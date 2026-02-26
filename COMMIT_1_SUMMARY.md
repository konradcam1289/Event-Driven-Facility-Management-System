# COMMIT 1: Shared Event Contracts Module

## ✅ Zrealizowane

### 1. Nowy moduł `contracts`
- Utworzono moduł Maven `contracts` jako część projektu multi-module
- Dodano do reactor build w root `pom.xml`
- Moduł zawiera wspólne kontrakty dla wszystkich serwisów

### 2. Struktura modułu contracts

```
contracts/
├── src/main/java/pl/konradcam/contracts/
│   ├── event/
│   │   ├── DomainEvent.java         (generyczny envelope dla eventów)
│   │   ├── DomainEventFactory.java   (fabryka do tworzenia eventów)
│   │   └── EventConstants.java       (centralne stałe: exchange, routing keys, queues)
│   └── reservation/
│       └── ReservationCreatedPayload.java (payload dla reservation.created)
└── pom.xml
```

### 3. Klasy w contracts

#### `DomainEvent<T>` - generyczny envelope
- `UUID eventId` - unikalny ID wydarzenia
- `String eventType` - typ eventu (np. "reservation.created")
- `String eventVersion` - wersja eventu (np. "1.0")
- `Instant occurredAt` - timestamp
- `UUID correlationId` - ID do śledzenia przepływu
- `T data` - payload
- Walidacja all fields (non-null)

#### `EventConstants` - centralne stałe
- `FACILITY_EVENTS_EXCHANGE = "facility.events"`
- `RESERVATION_CREATED = "reservation.created"`
- `EVENT_VERSION_V1 = "1.0"`
- `ROUTING_KEY_RESERVATION_CREATED = "reservation.created"`
- `QUEUE_NOTIFICATION = "notification.queue"`
- `QUEUE_REPORTING = "reporting.queue"`
- `ROUTING_PATTERN_ALL_RESERVATIONS = "reservation.*"`

#### `DomainEventFactory` - builder pattern
- `create()` - pełna wersja
- `createV1()` - uproszczona dla wersji 1.0
- Auto-generowanie `eventId` i `occurredAt`

#### `ReservationCreatedPayload`
- Payload dla eventu reservation.created
- Walidacja all fields

### 4. Refaktoryzacja serwisów

#### reservation-service
- ✅ Dodano dependency do `contracts`
- ✅ `ReservationEventPublisher` używa `DomainEvent` i `ReservationCreatedPayload`
- ✅ `RabbitMqConfig` używa `EventConstants`
- ✅ Usunięto stare klasy z pakietu `messaging.model`

#### notification-service
- ✅ Dodano dependency do `contracts`
- ✅ `ReservationEventsListener` używa `DomainEvent<ReservationCreatedPayload>`
- ✅ `RabbitMqConfig` używa `EventConstants.QUEUE_NOTIFICATION`
- ✅ Binding do `ROUTING_PATTERN_ALL_RESERVATIONS` (reservation.*)
- ✅ Usunięto stare klasy z pakietu `messaging.model`

#### reporting-service
- ✅ Dodano dependency do `contracts`
- ✅ `ReservationEventsListener` używa `DomainEvent<ReservationCreatedPayload>`
- ✅ `RabbitMqConfig` używa `EventConstants.QUEUE_REPORTING`
- ✅ Binding do `ROUTING_PATTERN_ALL_RESERVATIONS` (reservation.*)
- ✅ Usunięto stare klasy z pakietu `messaging.model`

### 5. Build status
- ✅ `mvn clean install` SUCCESS
- ✅ Wszystkie moduły kompilują się poprawnie
- ✅ Brak błędów kompilacji

## 🎯 Korzyści

1. **DRY (Don't Repeat Yourself)**
   - Event models zdefiniowane raz, używane wszędzie
   - Zmiana kontraktu w jednym miejscu

2. **Type Safety**
   - Wspólne typy dla publishera i consumerów
   - Kompilator wymusza zgodność

3. **Wersjonowanie**
   - `eventVersion` umożliwia ewolucję eventów
   - Przygotowanie na backward compatibility

4. **Centralne stałe**
   - Nazwy exchange/queues/routing keys w jednym miejscu
   - Łatwiejsze utrzymanie infrastruktury

5. **Portfolio value**
   - Pokazuje znajomość best practices w event-driven architecture
   - Demonstrates understanding of shared libraries in microservices
   - Clean separation of concerns

## 📦 Struktura eventów (standard)

Każdy event ma teraz strukturę:
```json
{
  "eventId": "uuid",
  "eventType": "reservation.created",
  "eventVersion": "1.0",
  "occurredAt": "2026-02-26T23:00:00Z",
  "correlationId": "uuid",
  "data": {
    "reservationId": "uuid",
    "roomId": "A101",
    "startAt": "2026-03-01T10:00:00",
    "endAt": "2026-03-01T12:00:00",
    "title": "Meeting",
    "createdBy": "john"
  }
}
```

## 🚀 Gotowe do commita

Commit message:
```
feat: add shared event contracts module

- Created contracts module with DomainEvent envelope
- Added EventConstants for centralized infrastructure config
- Implemented ReservationCreatedPayload
- Refactored all 3 services to use shared contracts
- Removed duplicate event models from services
- Added event versioning support
```

