package com.tokenqueue.token_queue_system.dto;

import jakarta.validation.constraints.NotNull;

public record BookTokenRequest(@NotNull Long serviceTypeId) {
}