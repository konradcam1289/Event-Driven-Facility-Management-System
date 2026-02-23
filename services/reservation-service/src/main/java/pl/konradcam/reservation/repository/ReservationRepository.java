package pl.konradcam.reservation.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.konradcam.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}

