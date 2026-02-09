package com.example.ems.controller;

import com.example.ems.dto.EmployeeCreateRequest;
import com.example.ems.dto.EmployeeResponse;
import com.example.ems.dto.EmployeeUpdateRequest;
import com.example.ems.security.SecurityUtil;
import com.example.ems.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmployeeResponse> list(@RequestParam Optional<Long> departmentId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "name") String sort) {
        return employeeService.list(departmentId, page, sort);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse create(@Valid @RequestBody EmployeeCreateRequest req) {
        return employeeService.create(req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public EmployeeResponse getById(@PathVariable Long id) {
        // USER can only view their own record (matches by email).
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            String username = SecurityUtil.currentUsername();
            EmployeeResponse me = employeeService.getById(employeeService.getByEmailOrThrow(username).getId());
            if (!me.getId().equals(id)) {
                throw new org.springframework.security.access.AccessDeniedException("USER can only view own info");
            }
        }
        return employeeService.getById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public EmployeeResponse me() {
        String username = SecurityUtil.currentUsername();
        // For ADMIN, you can still use /me if you set username to an employee email; else it errors.
        return employeeService.getById(employeeService.getByEmailOrThrow(username).getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest req) {
        return employeeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
