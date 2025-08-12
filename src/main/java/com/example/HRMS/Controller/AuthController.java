package com.example.HRMS.Controller;

import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Service.EmployeeAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthController {
    private final EmployeeAuthService employeeService;
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestBody LoginRequest loginRequest
    ){
        return employeeService.save(loginRequest);
    }

    @PostMapping("/auth/login/token:{token}")
    public ResponseEntity<?> login(
            @PathVariable String token
    ){
        return employeeService.login(token);
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<?> verify(
            @RequestBody LoginRequest loginRequest
    ){
        return employeeService.verify(loginRequest);
    }




}
