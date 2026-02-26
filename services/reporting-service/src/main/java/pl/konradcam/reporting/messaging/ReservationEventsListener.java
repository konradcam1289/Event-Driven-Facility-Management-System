package pl.konradcam.reporting.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.konradcam.contracts.event.DomainEvent;
import pl.konradcam.contracts.event.EventConstants;
import pl.konradcam.contracts.reservation.ReservationCreatedPayload;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.service.ReportingService;

@Component
public class ReservationEventsListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventsListener.class);

    private final ReportingService reportingService;

    public ReservationEventsListener(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @RabbitListener(queues = EventConstants.QUEUE_REPORTING)
    public void handleReservationCreated(DomainEvent<ReservationCreatedPayload> event) {
        logger.info("Received event: type={}, eventId={}, reservationId={}, version={}",
                event.eventType(), event.eventId(), event.data().reservationId(), event.eventVersion());

        // Idempotency check: skip if already processed
        if (reportingService.isEventAlreadyProcessed(event.eventId())) {
            logger.warn("Event {} already processed, skipping to maintain idempotency", event.eventId());
            return;
        }

        try {
            ReservationCreatedPayload data = event.data();

            ReservationReport report = new ReservationReport(
                    data.reservationId(),
                    data.roomId(),
                    data.startAt(),
                    data.endAt(),
                    data.title(),
                    data.createdBy()
            );

            // Save report and mark event as processed in single transaction
            reportingService.saveReservationReportAndMarkEventProcessed(
                    report,
                    event.eventId(),
                    event.eventType()
            );

            logger.info("Reservation report created successfully for reservation: {}", data.reservationId());
        } catch (Exception e) {
            logger.error("Error processing reservation.created event: eventId={}", event.eventId(), e);
            throw new RuntimeException("Failed to process reservation.created event", e);
        }
    }
}

