package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.BookTokenRequest;
import com.tokenqueue.token_queue_system.dto.TokenResponse;
import com.tokenqueue.token_queue_system.dto.TokenStatusResponse;
import com.tokenqueue.token_queue_system.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CITIZEN')")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponse> book(
            @Valid @RequestBody BookTokenRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tokenService.book(authentication.getName(), request.serviceTypeId()));
    }

    @GetMapping("/{id}/status")
    public TokenStatusResponse status(@PathVariable Long id,
                                      Authentication authentication) {
        return tokenService.getStatus(authentication.getName(), id);
    }
}