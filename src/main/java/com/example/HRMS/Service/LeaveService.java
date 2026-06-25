package com.example.HRMS.Service;

import com.example.HRMS.DTO.LeaveRequestDto;
import com.example.HRMS.DTO.LeaveResponse;
import com.example.HRMS.Entity.Employee;
import com.example.HRMS.Entity.LeaveRequest;
import com.example.HRMS.Entity.LeaveStatus;
import com.example.HRMS.Repositories.LeaveRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepo leaveRepo;

    private Employee currentEmployee() {
        return (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** Employee applies for leave. */
    public ResponseEntity<?> applyLeave(LeaveRequestDto dto) {
        LeaveRequest leave = LeaveRequest.builder()
                .employee(currentEmployee())
                .leaveType(dto.getLeaveType())
                .status(LeaveStatus.PENDING)
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .appliedAt(LocalDateTime.now())
                .build();
        LeaveRequest saved = leaveRepo.save(leave);
        return ResponseEntity.status(201).body(toResponse(saved));
    }

    /** Employee views their own leave history. */
    public ResponseEntity<List<LeaveResponse>> getMyLeaves() {
        Long id = currentEmployee().getId();
        List<LeaveResponse> list = leaveRepo.findByEmployee_Id(id)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** HR-only: view all pending leaves. */
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        List<LeaveResponse> list = leaveRepo.findByStatus(LeaveStatus.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** HR-only: view all leave requests. */
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        List<LeaveResponse> list = leaveRepo.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** HR approves a leave request. */
    public ResponseEntity<?> approveLeave(Long id) {
        return updateStatus(id, LeaveStatus.APPROVED);
    }

    /** HR rejects a leave request. */
    public ResponseEntity<?> rejectLeave(Long id) {
        return updateStatus(id, LeaveStatus.REJECTED);
    }

    private ResponseEntity<?> updateStatus(Long id, LeaveStatus status) {
        return leaveRepo.findById(id).map(leave -> {
            leave.setStatus(status);
            leave.setReviewedBy(currentEmployee());
            leave.setReviewedAt(LocalDateTime.now());
            leaveRepo.save(leave);
            return ResponseEntity.ok(toResponse(leave));
        }).orElse(ResponseEntity.notFound().build());
    }

    private LeaveResponse toResponse(LeaveRequest l) {
        return LeaveResponse.builder()
                .id(l.getId())
                .employeeName(l.getEmployee() != null ? l.getEmployee().getName() : null)
                .employeeEmail(l.getEmployee() != null ? l.getEmployee().getEmail() : null)
                .leaveType(l.getLeaveType())
                .status(l.getStatus())
                .fromDate(l.getFromDate())
                .toDate(l.getToDate())
                .reason(l.getReason())
                .appliedAt(l.getAppliedAt())
                .reviewedByName(l.getReviewedBy() != null ? l.getReviewedBy().getName() : null)
                .reviewedAt(l.getReviewedAt())
                .build();
    }
}
