package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.StaffQueueResponse;
import com.tokenqueue.token_queue_system.dto.StaffTokenResponse;
import com.tokenqueue.token_queue_system.service.StaffQueueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Staff Queue")
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class StaffQueueController {

    private final StaffQueueService staffQueueService;

    @GetMapping("/queue")
    public StaffQueueResponse getQueue(Authentication authentication) {
        return staffQueueService.getQueue(authentication.getName());
    }

    @PostMapping("/call-next")
    public StaffTokenResponse callNext(Authentication authentication) {
        return staffQueueService.callNext(authentication.getName());
    }

    @PostMapping("/tokens/{id}/start")
    public StaffTokenResponse start(
            Authentication authentication, @PathVariable Long id) {
        return staffQueueService.start(authentication.getName(), id);
    }

    @PostMapping("/tokens/{id}/complete")
    public StaffTokenResponse complete(
            Authentication authentication, @PathVariable Long id) {
        return staffQueueService.complete(authentication.getName(), id);
    }

    @PostMapping("/tokens/{id}/skip")
    public StaffTokenResponse skip(
            Authentication authentication, @PathVariable Long id) {
        return staffQueueService.skip(authentication.getName(), id);
    }
}
