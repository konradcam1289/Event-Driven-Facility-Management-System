package pl.konradcam.reporting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.konradcam.contracts.event.ProcessedEvent;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.repository.ProcessedEventRepository;
import pl.konradcam.reporting.repository.ReservationReportRepository;

import java.util.UUID;

@Service
public class ReportingService {
    private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);

    private final ReservationReportRepository reservationReportRepository;
    private final ProcessedEventRepository processedEventRepository;

    public ReportingService(
            ReservationReportRepository reservationReportRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.reservationReportRepository = reservationReportRepository;
        this.processedEventRepository = processedEventRepository;
    }

    /**
     * Saves reservation report and marks event as processed in a single transaction.
     * This ensures atomicity - either both succeed or both fail.
     */
    @Transactional
    public void saveReservationReportAndMarkEventProcessed(
            ReservationReport report,
            UUID eventId,
            String eventType
    ) {
        reservationReportRepository.save(report);

        ProcessedEvent processedEvent = new ProcessedEvent(eventId, eventType);
        processedEventRepository.save(processedEvent);

        logger.debug("Saved reservation report and marked event {} as processed", eventId);
    }

    public boolean isEventAlreadyProcessed(UUID eventId) {
        return processedEventRepository.existsByEventId(eventId);
    }
}

