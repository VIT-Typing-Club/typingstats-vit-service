package com.typingstatsvit.api.dto.typegg;

import jakarta.validation.constraints.NotBlank;

public record TypeggVerifyRequest(
        @NotBlank(message = "TypeGG username is required")
        String username
) {
}
