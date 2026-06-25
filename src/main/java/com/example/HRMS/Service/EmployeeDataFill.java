package com.example.HRMS.Service;

import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.Role;
import com.example.HRMS.Repositories.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Utility service for setting default field values on a newly created Employee.
 * Role defaults to EMPLOYEE; called after initial save.
 */
@Service
@RequiredArgsConstructor
public class EmployeeDataFill {

    private final EmployeeRepo employeeRepo;

    public Employee fillData(Employee employee) {
        if (employee.getRole() == null) {
            employee.setRole(Role.EMPLOYEE);
        }
        return employeeRepo.save(employee);
    }
}
