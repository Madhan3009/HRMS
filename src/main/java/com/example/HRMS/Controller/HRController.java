package com.example.HRMS.Controller;

import com.example.HRMS.DTO.OnboardRequest;
import com.example.HRMS.Service.OnBoardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
public class HRController {

    private final OnBoardingService onBoardingService;

    /** HR onboards a new employee by providing their profile details. */
    @PostMapping("/onboard")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> onboard(@RequestBody OnboardRequest request) {
        return onBoardingService.onboard(request);
    }
}
