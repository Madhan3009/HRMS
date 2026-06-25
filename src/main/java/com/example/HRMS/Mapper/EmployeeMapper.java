package com.example.HRMS.Mapper;

import com.example.HRMS.DTO.EmployeeProfileResponse;
import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.DTO.OnboardRequest;
import com.example.HRMS.Entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {
    Employee loginRequestToEmployee(LoginRequest loginRequest);
    Employee onboardRequestToEmployee(OnboardRequest onboardRequest);
    EmployeeProfileResponse employeeToProfileResponse(Employee employee);
}
