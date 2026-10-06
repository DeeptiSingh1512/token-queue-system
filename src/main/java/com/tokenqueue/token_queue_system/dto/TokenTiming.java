package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.enums.TokenStatus;

import java.time.LocalDateTime;

public record TokenTiming(
        LocalDateTime createdAt,
        LocalDateTime calledAt,
        LocalDateTime completedAt,
        TokenStatus status) {
}
