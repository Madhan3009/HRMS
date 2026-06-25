package com.example.HRMS.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private String designation;
}
