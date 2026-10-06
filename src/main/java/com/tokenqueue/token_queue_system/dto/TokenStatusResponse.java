package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.enums.TokenStatus;

public record TokenStatusResponse(
        Long tokenId,
        String tokenNumber,
        TokenStatus status,
        String serviceName,
        String officeName,
        long peopleAhead,
        Integer position,
        int estimatedWaitMinutes,
        String nowServing) {
}