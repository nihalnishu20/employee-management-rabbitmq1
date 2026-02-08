package com.example.ems.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notification.exchange";

    public static final String EMPLOYEE_QUEUE = "employee.queue";
    public static final String LEAVE_QUEUE = "leave.queue";

    public static final String EMPLOYEE_ROUTING_KEY = "employee.created";
    public static final String LEAVE_ROUTING_KEY = "leave.status.changed";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue employeeQueue() {
        return QueueBuilder.durable(EMPLOYEE_QUEUE).build();
    }

    @Bean
    public Queue leaveQueue() {
        return QueueBuilder.durable(LEAVE_QUEUE).build();
    }

    @Bean
    public Binding employeeBinding(Queue employeeQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(employeeQueue).to(notificationExchange).with(EMPLOYEE_ROUTING_KEY);
    }

    @Bean
    public Binding leaveBinding(Queue leaveQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(leaveQueue).to(notificationExchange).with(LEAVE_ROUTING_KEY);
    }

        @Bean
        public MessageConverter jsonMessageConverter() {
            return new Jackson2JsonMessageConverter();
        }

        @Bean
        public RabbitTemplate rabbitTemplate(
                ConnectionFactory connectionFactory,
                MessageConverter messageConverter) {

            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(messageConverter);
            return rabbitTemplate;
        }

}
