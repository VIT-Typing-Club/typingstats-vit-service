package com.typingstatsvit.api.dto;

import jakarta.validation.constraints.Pattern;

public record SendOtpRequest(
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@vit(ap)?student\\.ac\\.in$",
                message = "Must be a valid VIT email address"
        )
        String email
) {
}
