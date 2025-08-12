package com.example.HRMS.Service;

import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.DTO.LoginResponse;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Mapper.EmployeeMapper;
import com.example.HRMS.Repositories.EmployeeRepo;
import com.example.HRMS.config.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {
    private final EmployeeRepo employeeRepo;
    private final LoginResponse loginResponse;
    private final EmployeeMapper employeeMapper;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmployeeDataFill employeeDataFill;

    //Registration using LoginRequest DTO and generating JWT token and returning LoginResponse with Email and token
    public ResponseEntity<LoginResponse> save(LoginRequest loginRequest)
    {
        Employee emp = employeeMapper.DtoToEntity(loginRequest);
        emp.setPassword(passwordEncoder.encode( loginRequest.getPassword()));
        Employee savedUser = employeeRepo.save(emp);
        LoginResponse response = employeeMapper.EntityToDto(savedUser);
        response.setToken(jwtService.generateToken(loginRequest));
        employeeDataFill.fillData(savedUser);
        return ResponseEntity.ok(response);
    }

    //Extracting Email from the JWT token and validating the email with db
    public ResponseEntity<?> verify(LoginRequest loginRequest) {
       try{
           Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
           Authentication auth = authenticationManager.authenticate(authentication);
           SecurityContextHolder.getContext().setAuthentication(auth);
           String jwt = jwtService.generateToken(loginRequest);
           loginResponse.setEmail(loginRequest.getEmail());
           loginResponse.setToken(jwt);
           return ResponseEntity.ok(loginResponse);
       }
       catch(AuthenticationException e){
           return ResponseEntity.status(401).body("Invalid credentials:"+e.getMessage());
       }
    }

    public ResponseEntity<?> login(String token) {
        String email = jwtService.extractEmail(token);
        loginResponse.setEmail(email);
        loginResponse.setToken(token);
        return ResponseEntity.ok(loginResponse);
    }

}
