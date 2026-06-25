package com.example.HRMS.DTO;

import com.example.HRMS.Entity.LeaveStatus;
import com.example.HRMS.Entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponse {
    private Long id;
    private String employeeName;
    private String employeeEmail;
    private LeaveType leaveType;
    private LeaveStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private LocalDateTime appliedAt;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
}
