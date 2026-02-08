package com.example.ems.service;

import com.example.ems.dto.*;
import com.example.ems.entity.Employee;
import com.example.ems.entity.LeaveRequest;
import com.example.ems.entity.LeaveStatus;
import com.example.ems.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private static final Logger log = LoggerFactory.getLogger(LeaveService.class);

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeService employeeService;
    private final NotificationPublisher notificationPublisher;

    public LeaveService(LeaveRequestRepository leaveRepository,
                        EmployeeService employeeService,
                        NotificationPublisher notificationPublisher) {
        this.leaveRepository = leaveRepository;
        this.employeeService = employeeService;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public LeaveResponse submit(LeaveCreateRequest req) {
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new IllegalArgumentException("endDate must be >= startDate");
        }

        Employee emp = employeeService.getEntity(req.getEmployeeId());

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(emp);
        lr.setStartDate(req.getStartDate());
        lr.setEndDate(req.getEndDate());
        lr.setStatus(LeaveStatus.PENDING);
        lr.setReason(req.getReason());

        LeaveRequest saved = leaveRepository.save(lr);
        log.info("Leave request created: id={}, employeeId={}", saved.getId(), emp.getId());

        // No notification required on submit by spec; only status change.
        return toResponse(saved);
    }

    @Transactional
    public LeaveResponse updateStatus(Long leaveId, LeaveStatusUpdateRequest req) {
        LeaveRequest lr = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + leaveId));

        lr.setStatus(req.getStatus());
        LeaveRequest saved = leaveRepository.save(lr);

        NotificationLeaveStatusChanged msg = new NotificationLeaveStatusChanged();
        msg.setLeaveId(saved.getId());
        msg.setEmployeeId(saved.getEmployee().getId());
        msg.setEmployeeName(saved.getEmployee().getFullName());
        msg.setStartDate(saved.getStartDate());
        msg.setEndDate(saved.getEndDate());
        msg.setStatus(saved.getStatus());
        notificationPublisher.publishLeaveStatusChanged(msg);

        return toResponse(saved);
    }

    public List<LeaveResponse> listByEmployee(Long employeeId) {
        return leaveRepository.findByEmployee_IdOrderByCreatedAtDesc(employeeId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private LeaveResponse toResponse(LeaveRequest lr) {
        LeaveResponse r = new LeaveResponse();
        r.setId(lr.getId());
        r.setEmployeeId(lr.getEmployee().getId());
        r.setEmployeeName(lr.getEmployee().getFullName());
        r.setStartDate(lr.getStartDate());
        r.setEndDate(lr.getEndDate());
        r.setStatus(lr.getStatus());
        r.setReason(lr.getReason());
        r.setCreatedAt(lr.getCreatedAt());
        r.setUpdatedAt(lr.getUpdatedAt());
        return r;
    }
}
