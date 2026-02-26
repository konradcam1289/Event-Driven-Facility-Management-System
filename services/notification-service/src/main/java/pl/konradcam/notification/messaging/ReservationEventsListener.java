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
        logger.info("Received reservation.created event: eventId={}, reservationId={}",
                event.eventId(), event.data().reservationId());

        // Idempotencja: sprawdź czy event już przetworzony
        if (notificationService.isEventAlreadyProcessed(event.eventId())) {
            logger.warn("Event {} already processed, skipping", event.eventId());
            return;
        }

        try {
            ReservationCreatedPayload data = event.data();

            // Dla MVP: stubowy tekst powiadomienia
            String message = String.format(
                    "Rezerwacja: %s w sali %s od %s do %s",
                    data.title(), data.roomId(), data.startAt(), data.endAt()
            );

            Notification notification = new Notification(
                    data.reservationId(),
                    data.createdBy() + "@example.com", // Stub: używamy createdBy jako część emaila
                    message
            );

            notificationService.saveNotification(notification);
            notificationService.markEventProcessed(event.eventId(), event.eventType());

            logger.info("Notification created for reservation: {}", data.reservationId());
        } catch (Exception e) {
            logger.error("Error processing reservation.created event", e);
            throw new RuntimeException(e);
        }
    }
}

