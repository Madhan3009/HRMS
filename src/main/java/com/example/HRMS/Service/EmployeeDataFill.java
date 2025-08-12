package com.example.HRMS.Service;


import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.Role;
import com.example.HRMS.Repositories.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeDataFill {
    private final EmployeeRepo employeeRepo;

    public Employee fillData(Employee employee){
        employee.setRole(Role.EMPLOYEE);
        employee.isEnabled();
        return employeeRepo.save(employee);
    }
}

