package com.example.HRMS.Controller;

import com.example.HRMS.Service.OnBoardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HRController {
    private final OnBoardingService onBoardingService;
    @PostMapping("/onboard")
    public ResponseEntity<?> onboard(@RequestBody String email){
        return onBoardingService.onboard(email);
    }

}
