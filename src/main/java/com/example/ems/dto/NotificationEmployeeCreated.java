package com.example.ems.dto;

public class NotificationEmployeeCreated {
    private String eventType = "EMPLOYEE_CREATED";
    private Long employeeId;
    private String employeeName;
    private String department;
    private String email;

    public String getEventType() { return eventType; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
