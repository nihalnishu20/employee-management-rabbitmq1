package com.example.ems.dto;

import javax.validation.constraints.NotBlank;

public class DepartmentCreateRequest {
    @NotBlank
    private String name;
    private String location;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
