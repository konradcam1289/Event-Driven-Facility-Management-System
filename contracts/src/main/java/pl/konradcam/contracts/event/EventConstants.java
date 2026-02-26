package pl.konradcam.contracts.event;

/**
 * Centralized constants for RabbitMQ infrastructure.
 * All services must use these constants to ensure consistency.
 */
public final class EventConstants {

    private EventConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Exchange
    public static final String FACILITY_EVENTS_EXCHANGE = "facility.events";

    // Event Types
    public static final String RESERVATION_CREATED = "reservation.created";
    public static final String RESERVATION_UPDATED = "reservation.updated";
    public static final String RESERVATION_CANCELLED = "reservation.cancelled";

    // Event Versions
    public static final String EVENT_VERSION_V1 = "1.0";

    // Routing Keys
    public static final String ROUTING_KEY_RESERVATION_CREATED = "reservation.created";
    public static final String ROUTING_KEY_RESERVATION_UPDATED = "reservation.updated";
    public static final String ROUTING_KEY_RESERVATION_CANCELLED = "reservation.cancelled";

    // Queue Names
    public static final String QUEUE_NOTIFICATION = "notification.queue";
    public static final String QUEUE_REPORTING = "reporting.queue";

    // Queue Routing Patterns
    public static final String ROUTING_PATTERN_ALL_RESERVATIONS = "reservation.*";
}

