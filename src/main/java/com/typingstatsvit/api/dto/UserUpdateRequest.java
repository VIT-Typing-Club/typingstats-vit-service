package com.typingstatsvit.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Size(max = 50, message = "Display name cannot exceed 50 characters")
        String displayName,

        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@vit(ap)?student\\.ac\\.in$",
                message = "Must be a valid @vitstudent.ac.in or @vitapstudent.ac.in email address"
        )
        String collegeEmail,

        @Pattern(
                regexp = "^[a-zA-Z0-9-_]+$",
                message = "mt username can only contain letters, numbers, and hyphens"
        )
        String mtUrl,

        @Pattern(
                regexp = "^[a-zA-Z0-9-]+$",
                message = "LinkedIn username can only contain letters, numbers, and hyphens"
        )
        String linkedinUrl,

        @Pattern(
                regexp = "^[a-zA-Z0-9-]+$",
                message = "GitHub username can only contain letters, numbers, and hyphens"
        )
        String githubUrl
) {
}
