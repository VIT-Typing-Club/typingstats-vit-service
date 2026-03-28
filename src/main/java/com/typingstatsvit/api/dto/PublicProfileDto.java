package com.typingstatsvit.api.dto;

public record PublicProfileDto(
        String displayName,
        String discordUsername,
        String avatarUrl,
        String mtUrl,
        Boolean mtVerified,
        Boolean collegeVerified,
        String typeggUsername,
        String linkedinUrl,
        String githubUrl,
        String instagramUrl,
        String xUrl
) {
}
