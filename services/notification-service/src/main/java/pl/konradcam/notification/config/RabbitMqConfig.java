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
import pl.konradcam.contracts.event.EventConstants;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange facilityEventsExchange() {
        return new TopicExchange(EventConstants.FACILITY_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(EventConstants.QUEUE_NOTIFICATION).build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange facilityEventsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(facilityEventsExchange)
                .with(EventConstants.ROUTING_PATTERN_ALL_RESERVATIONS);
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

