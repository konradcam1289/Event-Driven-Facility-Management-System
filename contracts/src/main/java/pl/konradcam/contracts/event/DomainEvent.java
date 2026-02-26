package pl.konradcam.contracts.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Generic event envelope for all domain events in EDFMS.
 * This ensures consistent event structure across all services.
 *
 * @param <T> type of event payload
 */
public record DomainEvent<T>(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        UUID correlationId,
        T data
) {
    public DomainEvent {
        if (eventId == null) {
            throw new IllegalArgumentException("eventId cannot be null");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("eventType cannot be null or blank");
        }
        if (eventVersion == null || eventVersion.isBlank()) {
            throw new IllegalArgumentException("eventVersion cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
        if (correlationId == null) {
            throw new IllegalArgumentException("correlationId cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
    }
}

