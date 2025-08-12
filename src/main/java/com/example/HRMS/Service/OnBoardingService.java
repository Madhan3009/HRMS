package com.example.HRMS.Service;


import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.DTO.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnBoardingService {
    //Automate account registration
    private final EmployeeAuthService employeeAuthService;
    private final LoginRequest loginRequest;
    public ResponseEntity<?> onboard(String email) {
        loginRequest.setEmail(email);
        loginRequest.setPassword("<PASSWORD>");
        employeeAuthService.save(loginRequest);
        return employeeAuthService.save(loginRequest);
    }
}
