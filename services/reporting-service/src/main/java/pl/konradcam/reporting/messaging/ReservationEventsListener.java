package pl.konradcam.reporting.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.konradcam.reporting.config.RabbitMqConfig;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.messaging.model.ReservationCreatedData;
import pl.konradcam.reporting.messaging.model.ReservationEvent;
import pl.konradcam.reporting.service.ReportingService;

@Component
public class ReservationEventsListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventsListener.class);

    private final ReportingService reportingService;

    public ReservationEventsListener(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @RabbitListener(queues = RabbitMqConfig.RESERVATION_CREATED_QUEUE)
    public void handleReservationCreated(ReservationEvent<ReservationCreatedData> event) {
        logger.info("Received reservation.created event: eventId={}, reservationId={}",
                event.eventId(), event.data().reservationId());

        // Idempotencja: sprawdź czy event już przetworzony
        if (reportingService.isEventAlreadyProcessed(event.eventId())) {
            logger.warn("Event {} already processed, skipping", event.eventId());
            return;
        }

        try {
            ReservationCreatedData data = event.data();

            ReservationReport report = new ReservationReport(
                    data.reservationId(),
                    data.roomId(),
                    data.startAt(),
                    data.endAt(),
                    data.title(),
                    data.createdBy()
            );

            reportingService.saveReservationReport(report);
            reportingService.markEventProcessed(event.eventId(), event.eventType());

            logger.info("Reservation report created for reservation: {}", data.reservationId());
        } catch (Exception e) {
            logger.error("Error processing reservation.created event", e);
            throw new RuntimeException(e);
        }
    }
}

