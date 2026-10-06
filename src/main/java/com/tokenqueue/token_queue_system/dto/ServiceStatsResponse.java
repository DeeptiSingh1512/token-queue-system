package com.tokenqueue.token_queue_system.dto;

public record ServiceStatsResponse(
        Long serviceId,
        String name,
        String prefix,
        long issued,
        long waiting,
        long completed,
        long skipped,
        long cancelled) {
}
