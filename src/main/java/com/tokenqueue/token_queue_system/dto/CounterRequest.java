package com.tokenqueue.token_queue_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CounterRequest(
        @NotBlank @Size(max = 50) String name,
        Long staffId) {
}