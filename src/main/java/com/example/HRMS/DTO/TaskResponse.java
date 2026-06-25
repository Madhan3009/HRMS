package com.example.HRMS.DTO;

import com.example.HRMS.Entity.TaskPriority;
import com.example.HRMS.Entity.TaskStatus;
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
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime assignedAt;
    private String employeeName;
    private String employeeEmail;
    private String assignedByName;
}
