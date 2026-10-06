package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;

import java.time.LocalDate;

public record TokenResponse(
        Long id,
        String tokenNumber,
        Integer sequenceNumber,
        LocalDate tokenDate,
        TokenStatus status,
        Long serviceTypeId,
        String serviceTypeName,
        String officeName
) {

    public static TokenResponse from(Token token) {
        return new TokenResponse(
                token.getId(),
                token.getTokenNumber(),
                token.getSequenceNumber(),
                token.getTokenDate(),
                token.getStatus(),
                token.getServiceType().getId(),
                token.getServiceType().getName(),
                token.getServiceType().getOffice().getName()
        );
    }
}