package com.example.HRMS.Repositories;

import com.example.HRMS.Entity.LeaveRequest;
import com.example.HRMS.Entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepo extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee_Id(Long employeeId);
    List<LeaveRequest> findByStatus(LeaveStatus status);
}
