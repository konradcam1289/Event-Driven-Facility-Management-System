package pl.konradcam.contracts.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Factory for creating domain events with consistent structure.
 */
public final class DomainEventFactory {

    private DomainEventFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> DomainEvent<T> create(
            String eventType,
            String eventVersion,
            UUID correlationId,
            T data
    ) {
        return new DomainEvent<>(
                UUID.randomUUID(),
                eventType,
                eventVersion,
                Instant.now(),
                correlationId,
                data
        );
    }

    public static <T> DomainEvent<T> createV1(
            String eventType,
            UUID correlationId,
            T data
    ) {
        return create(eventType, EventConstants.EVENT_VERSION_V1, correlationId, data);
    }
}

