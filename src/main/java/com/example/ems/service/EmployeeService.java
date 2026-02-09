package com.example.ems.service;

import com.example.ems.dto.*;
import com.example.ems.entity.Department;
import com.example.ems.entity.Employee;
import com.example.ems.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final NotificationPublisher notificationPublisher;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentService departmentService,
                           NotificationPublisher notificationPublisher) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> list(Optional<Long> departmentId, int page, String sortBy) {
        if (page < 0) page = 0;

        Sort sort = resolveSort(sortBy);
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<Employee> result = departmentId
                .map(id -> employeeRepository.findByDepartment_Id(id, pageable))
                .orElseGet(() -> employeeRepository.findAll(pageable));

        return result.map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    public Employee getEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    public Employee getByEmailOrThrow(String email) {
        return employeeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found for email: " + email));
    }

    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest req) {
        Department dept = departmentService.getOrThrow(req.getDepartmentId());

        Employee e = new Employee();
        e.setFullName(req.getFullName());
        e.setEmail(req.getEmail());
        e.setDepartment(dept);
        e.setSalary(req.getSalary());
        e.setJoiningDate(req.getJoiningDate());

        Employee saved = employeeRepository.save(e);
        log.info("Employee created: id={}, email={}", saved.getId(), saved.getEmail());

        NotificationEmployeeCreated msg = new NotificationEmployeeCreated();
        msg.setEmployeeId(saved.getId());
        msg.setEmployeeName(saved.getFullName());
        msg.setDepartment(dept.getName());
        msg.setEmail(saved.getEmail());
        notificationPublisher.publishEmployeeCreated(msg);

        return toResponse(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest req) {
        Employee e = getEntity(id);
        Department dept = departmentService.getOrThrow(req.getDepartmentId());

        e.setFullName(req.getFullName());
        e.setEmail(req.getEmail());
        e.setDepartment(dept);
        e.setSalary(req.getSalary());
        e.setJoiningDate(req.getJoiningDate());

        Employee saved = employeeRepository.save(e);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Employee e = getEntity(id);
        employeeRepository.delete(e);
    }

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse r = new EmployeeResponse();
        r.setId(e.getId());
        r.setFullName(e.getFullName());
        r.setEmail(e.getEmail());
        if (e.getDepartment() != null) {
            r.setDepartmentId(e.getDepartment().getId());
            r.setDepartmentName(e.getDepartment().getName());
        }
        r.setSalary(e.getSalary());
        r.setJoiningDate(e.getJoiningDate());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) sortBy = "name";
        switch (sortBy) {
            case "department":
                return Sort.by("fullName"); // safe fallback
            case "joiningDate":
                return Sort.by(Sort.Direction.DESC, "joiningDate");
            case "name":
            default:
                return Sort.by("fullName");
        }
    }
}
