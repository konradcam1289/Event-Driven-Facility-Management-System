package pl.konradcam.contracts.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event payload for reservation created event.
 * This is shared between reservation-service (publisher) and consumers.
 */
public record ReservationCreatedPayload(
        UUID reservationId,
        String roomId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String title,
        String createdBy
) {
    public ReservationCreatedPayload {
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId cannot be null");
        }
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId cannot be null or blank");
        }
        if (startAt == null) {
            throw new IllegalArgumentException("startAt cannot be null");
        }
        if (endAt == null) {
            throw new IllegalArgumentException("endAt cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy cannot be null or blank");
        }
    }
}

