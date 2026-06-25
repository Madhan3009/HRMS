package com.example.HRMS.Repositories;

import com.example.HRMS.Entity.HR;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HRRepo extends JpaRepository<HR, Long> {
    Optional<HR> findByEmail(String email);
}
