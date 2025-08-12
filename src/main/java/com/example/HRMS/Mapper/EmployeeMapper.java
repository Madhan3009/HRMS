package com.example.HRMS.Mapper;

import com.example.HRMS.DTO.LoginRequest;
import com.example.HRMS.DTO.LoginResponse;
import com.example.HRMS.Entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee DtoToEntity (LoginRequest loginRequest);
    LoginResponse EntityToDto (Employee employee);
}
