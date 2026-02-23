package pl.konradcam.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String FACILITY_EVENTS_EXCHANGE = "facility.events";
    public static final String RESERVATION_CREATED_ROUTING_KEY = "reservation.created";
    public static final String RESERVATION_CREATED_QUEUE = "reservation.created.notification";

    @Bean
    public TopicExchange facilityEventsExchange() {
        return new TopicExchange(FACILITY_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue reservationCreatedQueue() {
        return QueueBuilder.durable(RESERVATION_CREATED_QUEUE).build();
    }

    @Bean
    public Binding reservationCreatedBinding(Queue reservationCreatedQueue, TopicExchange facilityEventsExchange) {
        return BindingBuilder.bind(reservationCreatedQueue)
                .to(facilityEventsExchange)
                .with(RESERVATION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}

