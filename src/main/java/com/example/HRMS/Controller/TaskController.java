package com.example.HRMS.Controller;

import com.example.HRMS.DTO.TaskRequest;
import com.example.HRMS.Entity.TaskStatus;
import com.example.HRMS.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** HR-only: create and assign a task to an employee. */
    @PostMapping
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    /** Returns the authenticated employee's own tasks. */
    @GetMapping("/my")
    public ResponseEntity<?> getMyTasks() {
        return taskService.getMyTasks();
    }

    /** HR-only: returns all tasks across all employees. */
    @GetMapping
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getAllTasks() {
        return taskService.getAllTasks();
    }

    /** Updates the status of a specific task. */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        return taskService.updateTaskStatus(id, status);
    }
}
