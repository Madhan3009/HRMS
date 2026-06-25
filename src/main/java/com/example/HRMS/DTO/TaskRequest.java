package com.example.HRMS.DTO;

import com.example.HRMS.Entity.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long employeeId;
}
