package pl.konradcam.reporting.messaging.model;

import java.time.Instant;
import java.util.UUID;

public record ReservationEvent<T>(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        UUID correlationId,
        T data
) {
}

