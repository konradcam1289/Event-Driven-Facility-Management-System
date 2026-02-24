package pl.konradcam.reporting.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.konradcam.reporting.domain.ReservationReport;

public interface ReservationReportRepository extends JpaRepository<ReservationReport, UUID> {
}

