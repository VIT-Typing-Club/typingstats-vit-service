package com.typingstatsvit.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmOtpRequest(
        @NotBlank(message = "Code is required")
        String code
) {
}
