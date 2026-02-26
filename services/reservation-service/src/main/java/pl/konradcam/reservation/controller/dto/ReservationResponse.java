package pl.konradcam.reservation.controller.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import pl.konradcam.reservation.domain.Reservation;
import pl.konradcam.reservation.domain.ReservationStatus;

public record ReservationResponse(
        UUID id,
        String roomId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String title,
        String createdBy,
        ReservationStatus status,
        Instant createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoomId(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getTitle(),
                reservation.getCreatedBy(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}

