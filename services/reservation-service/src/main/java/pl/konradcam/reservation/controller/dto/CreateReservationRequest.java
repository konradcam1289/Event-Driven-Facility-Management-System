package pl.konradcam.reservation.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotBlank(message = "roomId cannot be blank")
        String roomId,

        @NotNull(message = "startAt cannot be null")
        @FutureOrPresent(message = "startAt must be in future or present")
        LocalDateTime startAt,

        @NotNull(message = "endAt cannot be null")
        @Future(message = "endAt must be in future")
        LocalDateTime endAt,

        @NotBlank(message = "title cannot be blank")
        String title,

        @NotBlank(message = "createdBy cannot be blank")
        String createdBy
) {
}

