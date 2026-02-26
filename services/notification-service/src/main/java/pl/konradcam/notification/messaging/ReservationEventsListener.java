package pl.konradcam.notification.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.konradcam.contracts.event.DomainEvent;
import pl.konradcam.contracts.event.EventConstants;
import pl.konradcam.contracts.reservation.ReservationCreatedPayload;
import pl.konradcam.notification.domain.Notification;
import pl.konradcam.notification.service.NotificationService;

@Component
public class ReservationEventsListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventsListener.class);

    private final NotificationService notificationService;

    public ReservationEventsListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = EventConstants.QUEUE_NOTIFICATION)
    public void handleReservationCreated(DomainEvent<ReservationCreatedPayload> event) {
        logger.info("Received event: type={}, eventId={}, reservationId={}, version={}",
                event.eventType(), event.eventId(), event.data().reservationId(), event.eventVersion());

        // Idempotency check: skip if already processed
        if (notificationService.isEventAlreadyProcessed(event.eventId())) {
            logger.warn("Event {} already processed, skipping to maintain idempotency", event.eventId());
            return;
        }

        try {
            ReservationCreatedPayload data = event.data();

            // MVP: simple notification message
            String message = String.format(
                    "Rezerwacja: %s w sali %s od %s do %s",
                    data.title(), data.roomId(), data.startAt(), data.endAt()
            );

            Notification notification = new Notification(
                    data.reservationId(),
                    data.createdBy() + "@example.com",
                    message
            );

            // Save notification and mark event as processed in single transaction
            notificationService.saveNotificationAndMarkEventProcessed(
                    notification,
                    event.eventId(),
                    event.eventType()
            );

            logger.info("Notification created successfully for reservation: {}", data.reservationId());
        } catch (Exception e) {
            logger.error("Error processing reservation.created event: eventId={}", event.eventId(), e);
            throw new RuntimeException("Failed to process reservation.created event", e);
        }
    }
}

