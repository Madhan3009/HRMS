package com.example.HRMS.Repositories;

import com.example.HRMS.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query("""
            SELECT t FROM Token t
            INNER JOIN t.employee e
            WHERE e.id = :id
            AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAvailableTokenbyUser(Long id);

    Optional<Token> findByToken(String token);
}
