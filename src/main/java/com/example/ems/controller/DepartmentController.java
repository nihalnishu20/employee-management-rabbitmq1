package com.example.ems.controller;

import com.example.ems.dto.DepartmentCreateRequest;
import com.example.ems.dto.DepartmentResponse;
import com.example.ems.service.DepartmentService;
import com.example.ems.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DepartmentResponse> listAll() {
        return departmentService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse create(@Valid @RequestBody DepartmentCreateRequest req) {
        return departmentService.create(req);
    }

    @GetMapping("/{id}/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public Object employeesInDepartment(@PathVariable Long id,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "name") String sort) {
        return employeeService.list(java.util.Optional.of(id), page, sort);
    }
}
