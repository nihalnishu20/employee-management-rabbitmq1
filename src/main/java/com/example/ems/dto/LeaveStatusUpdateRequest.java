package com.example.ems.dto;

import com.example.ems.entity.LeaveStatus;

import javax.validation.constraints.NotNull;

public class LeaveStatusUpdateRequest {
    @NotNull
    private LeaveStatus status;

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
}
