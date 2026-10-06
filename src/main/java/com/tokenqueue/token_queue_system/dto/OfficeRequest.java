package com.tokenqueue.token_queue_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OfficeRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 255) String address,
        @Size(max = 100) String district) {
}