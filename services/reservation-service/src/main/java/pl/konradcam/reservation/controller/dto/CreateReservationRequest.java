package pl.konradcam.reservation.controller.dto;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        String roomId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String title,
        String createdBy
) {
}

