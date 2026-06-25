package com.example.HRMS.Service;

import com.example.HRMS.DTO.EmployeeProfileResponse;
import com.example.HRMS.DTO.UpdateProfileRequest;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Mapper.EmployeeMapper;
import com.example.HRMS.Repositories.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final EmployeeMapper employeeMapper;

    private Employee currentEmployee() {
        return (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** Returns the authenticated employee's own profile. */
    public ResponseEntity<EmployeeProfileResponse> getMyProfile() {
        Employee emp = currentEmployee();
        return ResponseEntity.ok(employeeMapper.employeeToProfileResponse(emp));
    }

    /** HR-only: returns all employees. */
    public ResponseEntity<List<EmployeeProfileResponse>> getAllEmployees() {
        List<EmployeeProfileResponse> list = employeeRepo.findAll()
                .stream()
                .map(employeeMapper::employeeToProfileResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** HR-only: returns a single employee by ID. */
    public ResponseEntity<?> getEmployeeById(Long id) {
        return employeeRepo.findById(id)
                .map(emp -> ResponseEntity.ok(employeeMapper.employeeToProfileResponse(emp)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Employee updates their own name and phone. */
    public ResponseEntity<?> updateMyProfile(UpdateProfileRequest request) {
        Employee emp = currentEmployee();
        if (request.getName() != null) emp.setName(request.getName());
        if (request.getPhone() != null) emp.setPhone(request.getPhone());
        employeeRepo.save(emp);
        return ResponseEntity.ok(employeeMapper.employeeToProfileResponse(emp));
    }
}
