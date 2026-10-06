package com.tokenqueue.token_queue_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ServiceTypeRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "^[A-Za-z]{1,5}$", message = "Prefix must be 1 to 5 letters")
        String prefix,
        @Min(1) @Max(240) int avgServiceMinutes) {
}