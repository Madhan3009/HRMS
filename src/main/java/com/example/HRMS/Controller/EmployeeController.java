package com.example.HRMS.Controller;

import com.example.HRMS.DTO.UpdateProfileRequest;
import com.example.HRMS.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /** Returns the authenticated employee's own profile. */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        return employeeService.getMyProfile();
    }

    /** Updates the authenticated employee's own name and phone. */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return employeeService.updateMyProfile(request);
    }

    /** HR-only: lists all employees. */
    @GetMapping
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /** HR-only: fetch a specific employee by ID. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }
}
