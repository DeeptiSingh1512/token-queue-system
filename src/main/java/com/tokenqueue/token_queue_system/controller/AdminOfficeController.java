package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.OfficeRequest;
import com.tokenqueue.token_queue_system.dto.OfficeResponse;
import com.tokenqueue.token_queue_system.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Admin Offices")
@RequestMapping("/api/admin/offices")
@RequiredArgsConstructor
public class AdminOfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody OfficeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(officeService.create(request));
    }

    @GetMapping
    public List<OfficeResponse> getAll() {
        return officeService.getAll();
    }

    @GetMapping("/{id}")
    public OfficeResponse getById(@PathVariable Long id) {
        return officeService.getById(id);
    }

    @PutMapping("/{id}")
    public OfficeResponse update(@PathVariable Long id,
                                 @Valid @RequestBody OfficeRequest request) {
        return officeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        officeService.disable(id);
        return ResponseEntity.noContent().build();
    }
}