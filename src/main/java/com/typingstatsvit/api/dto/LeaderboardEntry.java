package com.typingstatsvit.api.dto;

import com.typingstatsvit.api.entity.TestType;

import java.time.LocalDateTime;

public record LeaderboardEntry(
        String discordId,
        String username,
        String avatarUrl,
        Double wpm,
        Double accuracy,
        Double raw,
        TestType testType,
        LocalDateTime createdAt
) {
}
