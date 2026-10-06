package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.enums.TokenStatus;

public record TokenStatusCount(Long serviceId, TokenStatus status, Long count) {
}
