package com.typingstatsvit.api.dto.typegg;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TypeggVerifyRequest(
        @NotBlank(message = "TypeGG username is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9-_]+$",
                message = "TypeGG username can only contain letters, numbers, and hyphens"
        )
        String typeggUsername
) {
}
