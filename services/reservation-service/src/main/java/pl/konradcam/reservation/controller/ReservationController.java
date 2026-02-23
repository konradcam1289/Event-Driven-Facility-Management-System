package pl.konradcam.reservation.controller;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradcam.reservation.controller.dto.CreateReservationRequest;
import pl.konradcam.reservation.controller.dto.ReservationResponse;
import pl.konradcam.reservation.domain.Reservation;
import pl.konradcam.reservation.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateReservationRequest request) {
        if (!isValidTimeRange(request.startAt(), request.endAt())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "startAt must be before endAt"));
        }

        Reservation reservation = reservationService.createReservation(
                request.roomId(),
                request.startAt(),
                request.endAt(),
                request.title(),
                request.createdBy()
        );

        ReservationResponse response = ReservationResponse.from(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private boolean isValidTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        return startAt != null && endAt != null && startAt.isBefore(endAt);
    }
}

