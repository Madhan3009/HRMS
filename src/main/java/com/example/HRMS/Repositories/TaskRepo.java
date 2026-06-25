package com.example.HRMS.Repositories;

import com.example.HRMS.Entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepo extends JpaRepository<Tasks, Long> {
    List<Tasks> findByEmployee_Id(Long employeeId);
}
