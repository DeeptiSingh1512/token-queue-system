package com.tokenqueue.token_queue_system.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BoardResponse(
        Long officeId,
        String officeName,
        LocalDateTime updatedAt,
        List<BoardCounterResponse> counters,
        List<BoardServiceResponse> services) {
}