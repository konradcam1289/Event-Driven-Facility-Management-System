package pl.konradcam.reservation.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.konradcam.contracts.event.DomainEvent;
import pl.konradcam.contracts.event.DomainEventFactory;
import pl.konradcam.contracts.event.EventConstants;
import pl.konradcam.contracts.reservation.ReservationCreatedPayload;
import pl.konradcam.reservation.domain.Reservation;

@Component
public class ReservationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReservationCreated(Reservation reservation) {
        ReservationCreatedPayload payload = new ReservationCreatedPayload(
                reservation.getId(),
                reservation.getRoomId(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getTitle(),
                reservation.getCreatedBy()
        );

        DomainEvent<ReservationCreatedPayload> event = DomainEventFactory.createV1(
                EventConstants.RESERVATION_CREATED,
                reservation.getId(),
                payload
        );

        rabbitTemplate.convertAndSend(
                EventConstants.FACILITY_EVENTS_EXCHANGE,
                EventConstants.ROUTING_KEY_RESERVATION_CREATED,
                event
        );
    }
}

