package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.StaffRequest;
import com.tokenqueue.token_queue_system.dto.StaffResponse;
import com.tokenqueue.token_queue_system.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponse> create(@Valid @RequestBody StaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(request));
    }

    @GetMapping
    public List<StaffResponse> getAll() {
        return staffService.getAll();
    }
}