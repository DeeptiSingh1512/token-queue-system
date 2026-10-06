package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.ServiceTypeRequest;
import com.tokenqueue.token_queue_system.dto.ServiceTypeResponse;
import com.tokenqueue.token_queue_system.service.ServiceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @PostMapping("/offices/{officeId}/services")
    public ResponseEntity<ServiceTypeResponse> create(
            @PathVariable Long officeId,
            @Valid @RequestBody ServiceTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceTypeService.create(officeId, request));
    }

    @GetMapping("/offices/{officeId}/services")
    public List<ServiceTypeResponse> getByOffice(@PathVariable Long officeId) {
        return serviceTypeService.getByOffice(officeId);
    }

    @PutMapping("/services/{id}")
    public ServiceTypeResponse update(@PathVariable Long id,
                                      @Valid @RequestBody ServiceTypeRequest request) {
        return serviceTypeService.update(id, request);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        serviceTypeService.disable(id);
        return ResponseEntity.noContent().build();
    }
}