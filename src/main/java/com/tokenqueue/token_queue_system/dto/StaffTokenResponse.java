package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;

import java.time.LocalDateTime;

public record StaffTokenResponse(
        Long id,
        String tokenNumber,
        TokenStatus status,
        String serviceName,
        String citizenName,
        LocalDateTime calledAt) {

    public static StaffTokenResponse from(Token token) {
        return new StaffTokenResponse(
                token.getId(),
                token.getTokenNumber(),
                token.getStatus(),
                token.getServiceType().getName(),
                token.getCitizen().getName(),
                token.getCalledAt());
    }
}
