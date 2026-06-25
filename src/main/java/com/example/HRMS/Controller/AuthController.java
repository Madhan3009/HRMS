package com.example.HRMS.Controller;

import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.Service.EmployeeAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeAuthService employeeAuthService;

    /** Self-registration: provide email + password, get JWT back. */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest loginRequest) {
        return employeeAuthService.register(loginRequest);
    }

    /** Standard login: provide email + password, get JWT back. */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return employeeAuthService.login(loginRequest);
    }
}
