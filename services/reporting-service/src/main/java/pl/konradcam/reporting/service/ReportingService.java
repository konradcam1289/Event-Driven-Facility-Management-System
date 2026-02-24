package pl.konradcam.reporting.service;

import org.springframework.stereotype.Service;
import pl.konradcam.reporting.domain.ProcessedEvent;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.repository.ProcessedEventRepository;
import pl.konradcam.reporting.repository.ReservationReportRepository;

@Service
public class ReportingService {
    private final ReservationReportRepository reservationReportRepository;
    private final ProcessedEventRepository processedEventRepository;

    public ReportingService(
            ReservationReportRepository reservationReportRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.reservationReportRepository = reservationReportRepository;
        this.processedEventRepository = processedEventRepository;
    }

    public void saveReservationReport(ReservationReport report) {
        reservationReportRepository.save(report);
    }

    public void markEventProcessed(java.util.UUID eventId, String eventType) {
        ProcessedEvent processedEvent = new ProcessedEvent(eventId, eventType);
        processedEventRepository.save(processedEvent);
    }

    public boolean isEventAlreadyProcessed(java.util.UUID eventId) {
        return processedEventRepository.findByEventId(eventId).isPresent();
    }
}

