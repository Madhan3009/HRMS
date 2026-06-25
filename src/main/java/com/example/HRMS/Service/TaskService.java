package com.example.HRMS.Service;

import com.example.HRMS.DTO.TaskRequest;
import com.example.HRMS.DTO.TaskResponse;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.TaskStatus;
import com.example.HRMS.Entity.Tasks;
import com.example.HRMS.Repositories.EmployeeRepo;
import com.example.HRMS.Repositories.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;
    private final EmployeeRepo employeeRepo;

    private Employee currentEmployee() {
        return (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** HR creates and assigns a task to an employee. */
    public ResponseEntity<?> createTask(TaskRequest request) {
        Employee assignee = employeeRepo.findById(request.getEmployeeId())
                .orElse(null);
        if (assignee == null) {
            return ResponseEntity.badRequest().body("Employee not found with id: " + request.getEmployeeId());
        }
        Tasks task = Tasks.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .status(TaskStatus.TODO)
                .assignedAt(LocalDateTime.now())
                .employee(assignee)
                .assignedBy(currentEmployee())
                .build();
        Tasks saved = taskRepo.save(task);
        return ResponseEntity.status(201).body(toResponse(saved));
    }

    /** Returns the authenticated employee's own tasks. */
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        Long id = currentEmployee().getId();
        List<TaskResponse> list = taskRepo.findByEmployee_Id(id)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** HR-only: returns all tasks. */
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> list = taskRepo.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** Employee or HR updates a task's status. */
    public ResponseEntity<?> updateTaskStatus(Long taskId, TaskStatus status) {
        return taskRepo.findById(taskId).map(task -> {
            task.setStatus(status);
            taskRepo.save(task);
            return ResponseEntity.ok(toResponse(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    private TaskResponse toResponse(Tasks t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .assignedAt(t.getAssignedAt())
                .employeeName(t.getEmployee() != null ? t.getEmployee().getName() : null)
                .employeeEmail(t.getEmployee() != null ? t.getEmployee().getEmail() : null)
                .assignedByName(t.getAssignedBy() != null ? t.getAssignedBy().getName() : null)
                .build();
    }
}
