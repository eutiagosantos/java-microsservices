package br.com.alurafood.pagamentos.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class PaymentAmqpConfig {

    static final String queueName = "payments.completed";

    @Bean
    public Queue createQueue() {
        return QueueBuilder.nonDurable(queueName).build();
    }

    @Bean
    public RabbitAdmin createRabbitAdmin(@NonNull ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory conn, Jackson2JsonMessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate(conn);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
