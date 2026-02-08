package com.example.ems.service;

import com.example.ems.dto.DepartmentCreateRequest;
import com.example.ems.dto.DepartmentResponse;
import com.example.ems.entity.Department;
import com.example.ems.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentResponse create(DepartmentCreateRequest req) {
        Department d = new Department(req.getName(), req.getLocation());
        Department saved = departmentRepository.save(d);
        return toResponse(saved);
    }

    public List<DepartmentResponse> listAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Department getOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + id));
    }

    private DepartmentResponse toResponse(Department d) {
        DepartmentResponse r = new DepartmentResponse();
        r.setId(d.getId());
        r.setName(d.getName());
        r.setLocation(d.getLocation());
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());
        return r;
    }
}
