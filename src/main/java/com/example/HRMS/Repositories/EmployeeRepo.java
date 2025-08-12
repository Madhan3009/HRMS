package com.example.HRMS.Repositories;

import com.example.HRMS.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee,Integer> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmailAndPassword(String email,String password);

}
