package pl.konradcam.reporting.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.konradcam.reporting.config.RabbitMqConfig;

@Component
public class ReservationEventsListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationEventsListener.class);

    @RabbitListener(queues = RabbitMqConfig.RESERVATION_CREATED_QUEUE)
    public void onReservationCreated(JsonNode event) {
        LOGGER.info("Reservation event received: {}", event);
    }
}

