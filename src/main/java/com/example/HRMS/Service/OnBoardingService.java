package com.example.HRMS.Service;

import com.example.HRMS.DTO.OnboardRequest;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.Role;
import com.example.HRMS.Mapper.EmployeeMapper;
import com.example.HRMS.Repositories.EmployeeRepo;
import com.example.HRMS.config.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnBoardingService {

    private final EmployeeRepo employeeRepo;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeAuthService employeeAuthService;
    private final JWTService jwtService;
    private final EmailService emailService;

    /**
     * HR onboards a new employee. A secure temporary password is auto-generated
     * and returned in the response (in production, this would be emailed).
     */
    public ResponseEntity<?> onboard(OnboardRequest request) {
        if (employeeRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Email already registered.");
        }

        // Generate a secure temporary password
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);

        Employee emp = employeeMapper.onboardRequestToEmployee(request);
        emp.setPassword(passwordEncoder.encode(tempPassword));
        emp.setRole(Role.EMPLOYEE);
        emp.setDateOfJoining(LocalDate.now());

        Employee saved = employeeRepo.save(emp);

        // Issue a JWT and persist the token
        String jwt = jwtService.generateToken(saved);
        employeeAuthService.saveToken(saved, jwt);

        // Dispatch email
        emailService.sendTemporaryPassword(saved.getEmail(), saved.getName(), tempPassword);

        return ResponseEntity.ok(Map.of(
                "message", "Employee onboarded successfully.",
                "email", saved.getEmail(),
                "temporaryPassword", tempPassword,
                "token", jwt
        ));
    }
}
