package com.typingstatsvit.api.dto.monkeytype;

import jakarta.validation.constraints.NotBlank;

public record MtVerifyRequest(
        @NotBlank(message = "Username is required")
        String username
) {
}
