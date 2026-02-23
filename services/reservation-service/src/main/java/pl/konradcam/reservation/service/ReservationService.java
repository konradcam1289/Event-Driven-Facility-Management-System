package pl.konradcam.reservation.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import pl.konradcam.reservation.domain.Reservation;
import pl.konradcam.reservation.messaging.ReservationEventPublisher;
import pl.konradcam.reservation.repository.ReservationRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationEventPublisher reservationEventPublisher;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationEventPublisher reservationEventPublisher
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationEventPublisher = reservationEventPublisher;
    }

    public Reservation createReservation(
            String roomId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String title,
            String createdBy
    ) {
        Reservation reservation = new Reservation(roomId, startAt, endAt, title, createdBy);
        Reservation saved = reservationRepository.save(reservation);
        reservationEventPublisher.publishReservationCreated(saved);
        return saved;
    }
}

