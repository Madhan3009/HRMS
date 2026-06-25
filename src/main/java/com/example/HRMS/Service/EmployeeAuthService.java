package com.example.HRMS.Service;

import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.DTO.LoginResponse;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.Role;
import com.example.HRMS.Entity.Token;
import com.example.HRMS.Entity.TokenType;
import com.example.HRMS.Mapper.EmployeeMapper;
import com.example.HRMS.Repositories.EmployeeRepo;
import com.example.HRMS.Repositories.TokenRepo;
import com.example.HRMS.config.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {

    private final EmployeeRepo employeeRepo;
    private final EmployeeMapper employeeMapper;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepo tokenRepo;

    /** Self-registration endpoint — assigns EMPLOYEE role by default. */
    public ResponseEntity<?> register(LoginRequest loginRequest) {
        if (employeeRepo.findByEmail(loginRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Email already registered.");
        }
        Employee emp = employeeMapper.loginRequestToEmployee(loginRequest);
        emp.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        emp.setRole(Role.EMPLOYEE);
        Employee saved = employeeRepo.save(emp);
        String jwt = jwtService.generateToken(saved);
        saveToken(saved, jwt);
        return ResponseEntity.ok(new LoginResponse(saved.getEmail(), jwt));
    }

    /** Standard login with email + password. */
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
            Employee employee = (Employee) auth.getPrincipal();
            revokeAllUserTokens(employee);
            String jwt = jwtService.generateToken(employee);
            saveToken(employee, jwt);
            return ResponseEntity.ok(new LoginResponse(employee.getEmail(), jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
        }
    }

    // ── Token helpers ────────────────────────────────────────────────────────

    public void saveToken(Employee employee, String jwt) {
        Token token = Token.builder()
                .employee(employee)
                .token(jwt)
                .tokenType(TokenType.Bearer)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(Employee employee) {
        List<Token> validTokens = tokenRepo.findAvailableTokenbyUser(employee.getId());
        if (validTokens.isEmpty()) return;
        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepo.saveAll(validTokens);
    }
}
