package pl.konradcam.reservation.messaging;

import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.konradcam.reservation.config.RabbitMqConfig;
import pl.konradcam.reservation.domain.Reservation;
import pl.konradcam.reservation.messaging.model.ReservationCreatedData;
import pl.konradcam.reservation.messaging.model.ReservationEvent;

@Component
public class ReservationEventPublisher {
    private static final String RESERVATION_CREATED_EVENT_TYPE = "reservation.created";

    private final RabbitTemplate rabbitTemplate;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReservationCreated(Reservation reservation) {
        ReservationCreatedData data = new ReservationCreatedData(
                reservation.getId(),
                reservation.getRoomId(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getTitle(),
                reservation.getCreatedBy()
        );

        ReservationEvent<ReservationCreatedData> event = new ReservationEvent<>(
                UUID.randomUUID(),
                RESERVATION_CREATED_EVENT_TYPE,
                Instant.now(),
                reservation.getId(),
                data
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.FACILITY_EVENTS_EXCHANGE,
                RabbitMqConfig.RESERVATION_CREATED_ROUTING_KEY,
                event
        );
    }
}

