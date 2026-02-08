package com.example.ems.consumer;

import com.example.ems.config.RabbitMQConfig;
import com.example.ems.dto.NotificationEmployeeCreated;
import com.example.ems.dto.NotificationLeaveStatusChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.EMPLOYEE_QUEUE)
    public void onEmployeeCreated(NotificationEmployeeCreated msg) {
        log.info("[CONSUMED] EMPLOYEE_CREATED: id={}, name={}, dept={}, email={}",
                msg.getEmployeeId(), msg.getEmployeeName(), msg.getDepartment(), msg.getEmail());
        simulateEmail("Welcome", msg.getEmail(), "Welcome " + msg.getEmployeeName() + " to " + msg.getDepartment());
    }

    @RabbitListener(queues = RabbitMQConfig.LEAVE_QUEUE)
    public void onLeaveStatusChanged(NotificationLeaveStatusChanged msg) {
        log.info("[CONSUMED] LEAVE_STATUS_CHANGED: leaveId={}, employee={}, dates={} to {}, status={}",
                msg.getLeaveId(), msg.getEmployeeName(), msg.getStartDate(), msg.getEndDate(), msg.getStatus());
        simulateEmail("Leave Status Update", "(employee-email-not-stored-here)",
                "Leave #" + msg.getLeaveId() + " is now " + msg.getStatus());
    }

    private void simulateEmail(String subject, String to, String body) {
        // Basic failure simulation: if "fail" is in subject, throw.
        try {
            log.info("Simulating email send | subject='{}' | to='{}' | body='{}'", subject, to, body);
        } catch (Exception ex) {
            log.error("Failed to simulate email", ex);
        }
    }
}
