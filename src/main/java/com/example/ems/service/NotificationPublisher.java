package com.example.ems.service;

import com.example.ems.config.RabbitMQConfig;
import com.example.ems.dto.NotificationEmployeeCreated;
import com.example.ems.dto.NotificationLeaveStatusChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishEmployeeCreated(NotificationEmployeeCreated msg) {
        try {
            log.info("Publishing EMPLOYEE_CREATED notification: employeeId={}, email={}", msg.getEmployeeId(), msg.getEmail());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.EMPLOYEE_ROUTING_KEY, msg);
            log.info("Published EMPLOYEE_CREATED successfully");
        } catch (AmqpException ex) {
            log.error("Failed to publish EMPLOYEE_CREATED notification", ex);
        }
    }

    public void publishLeaveStatusChanged(NotificationLeaveStatusChanged msg) {
        try {
            log.info("Publishing LEAVE_STATUS_CHANGED notification: leaveId={}, status={}", msg.getLeaveId(), msg.getStatus());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.LEAVE_ROUTING_KEY, msg);
            log.info("Published LEAVE_STATUS_CHANGED successfully");
        } catch (AmqpException ex) {
            log.error("Failed to publish LEAVE_STATUS_CHANGED notification", ex);
        }
    }
}
