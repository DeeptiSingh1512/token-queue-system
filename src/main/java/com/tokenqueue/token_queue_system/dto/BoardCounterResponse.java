package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.enums.TokenStatus;

public record BoardCounterResponse(
        Long counterId,
        String counterName,
        String currentToken,
        TokenStatus status) {
}