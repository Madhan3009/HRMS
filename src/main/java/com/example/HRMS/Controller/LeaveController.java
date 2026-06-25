package com.example.HRMS.Controller;

import com.example.HRMS.DTO.LeaveRequestDto;
import com.example.HRMS.Service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    /** Employee applies for leave. */
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequestDto dto) {
        return leaveService.applyLeave(dto);
    }

    /** Returns the authenticated employee's own leave history. */
    @GetMapping("/my")
    public ResponseEntity<?> getMyLeaves() {
        return leaveService.getMyLeaves();
    }

    /** HR-only: returns all pending leave requests. */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getPendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    /** HR-only: returns all leave requests. */
    @GetMapping
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    /** HR approves a leave request by ID. */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        return leaveService.approveLeave(id);
    }

    /** HR rejects a leave request by ID. */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('HR')")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        return leaveService.rejectLeave(id);
    }
}
