package com.example.HRMS.DTO;

import com.example.HRMS.Entity.Role;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Setter
@Getter
public class LoginRequest {
    String email;
    String password;
    Role role;
}

