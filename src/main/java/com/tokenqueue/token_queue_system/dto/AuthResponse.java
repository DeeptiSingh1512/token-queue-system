package com.tokenqueue.token_queue_system.dto;

public record AuthResponse(
        String token,
        String name,
        String email,
        String role) {
}