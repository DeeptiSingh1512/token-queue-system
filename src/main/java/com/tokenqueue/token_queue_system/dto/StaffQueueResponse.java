package com.tokenqueue.token_queue_system.dto;

import java.util.List;

public record StaffQueueResponse(
        Long counterId,
        String counterName,
        String officeName,
        StaffTokenResponse currentToken,
        long waitingCount,
        List<String> nextTokens) {
}
