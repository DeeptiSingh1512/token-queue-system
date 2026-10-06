package com.tokenqueue.token_queue_system.dto;

public record BoardServiceResponse(
        Long serviceId,
        String name,
        String prefix,
        long waitingCount,
        int estimatedWaitMinutes) {
}