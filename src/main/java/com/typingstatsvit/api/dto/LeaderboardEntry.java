package com.typingstatsvit.api.dto;

import com.typingstatsvit.api.entity.TestType;

import java.time.Instant;

public record LeaderboardEntry(
        String discordId,
        String displayName,
        String username,
        String avatarUrl,
        Double wpm,
        Double accuracy,
        Double raw,
        TestType testType,
        Instant createdAt
) {
}
