package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.CounterRequest;
import com.tokenqueue.token_queue_system.dto.CounterResponse;
import com.tokenqueue.token_queue_system.service.CounterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Admin Counters")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCounterController {

    private final CounterService counterService;

    @PostMapping("/offices/{officeId}/counters")
    public ResponseEntity<CounterResponse> create(
            @PathVariable Long officeId,
            @Valid @RequestBody CounterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(counterService.create(officeId, request));
    }

    @GetMapping("/offices/{officeId}/counters")
    public List<CounterResponse> getByOffice(@PathVariable Long officeId) {
        return counterService.getByOffice(officeId);
    }

    @PutMapping("/counters/{id}")
    public CounterResponse update(@PathVariable Long id,
                                  @Valid @RequestBody CounterRequest request) {
        return counterService.update(id, request);
    }

    @DeleteMapping("/counters/{id}")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        counterService.disable(id);
        return ResponseEntity.noContent().build();
    }
}