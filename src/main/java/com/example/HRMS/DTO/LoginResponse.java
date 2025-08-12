package com.example.HRMS.DTO;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Setter
@Getter
public class LoginResponse {
    String Email;
    String token;
}
