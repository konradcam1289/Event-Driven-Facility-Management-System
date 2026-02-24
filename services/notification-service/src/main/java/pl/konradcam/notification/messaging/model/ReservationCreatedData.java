package pl.konradcam.notification.messaging.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedData(
        UUID reservationId,
        String roomId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String title,
        String createdBy
) {
}

