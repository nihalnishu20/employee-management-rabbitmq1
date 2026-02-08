package com.example.ems.controller;

import com.example.ems.dto.LeaveCreateRequest;
import com.example.ems.dto.LeaveResponse;
import com.example.ems.dto.LeaveStatusUpdateRequest;
import com.example.ems.security.SecurityUtil;
import com.example.ems.service.EmployeeService;
import com.example.ems.service.LeaveService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    public LeaveController(LeaveService leaveService, EmployeeService employeeService) {
        this.leaveService = leaveService;
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public LeaveResponse submit(@Valid @RequestBody LeaveCreateRequest req) {
        // USER can only apply for self (username == employee email)
        if (isUser()) {
            String username = SecurityUtil.currentUsername();
            Long myId = employeeService.getByEmailOrThrow(username).getId();
            if (!myId.equals(req.getEmployeeId())) {
                throw new org.springframework.security.access.AccessDeniedException("USER can only apply for own leave");
            }
        }
        return leaveService.submit(req);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public LeaveResponse updateStatus(@PathVariable Long id, @Valid @RequestBody LeaveStatusUpdateRequest req) {
        return leaveService.updateStatus(id, req);
    }

    @GetMapping("/employee/{empId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<LeaveResponse> listByEmployee(@PathVariable Long empId) {
        if (isUser()) {
            String username = SecurityUtil.currentUsername();
            Long myId = employeeService.getByEmailOrThrow(username).getId();
            if (!myId.equals(empId)) {
                throw new org.springframework.security.access.AccessDeniedException("USER can only view own leaves");
            }
        }
        return leaveService.listByEmployee(empId);
    }

    private boolean isUser() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }
}
