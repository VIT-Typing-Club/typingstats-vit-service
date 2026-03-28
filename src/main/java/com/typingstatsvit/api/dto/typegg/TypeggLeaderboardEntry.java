package com.typingstatsvit.api.dto.typegg;

public record TypeggLeaderboardEntry(
        String discordId,
        String discordUsername,
        String displayName,
        String avatarUrl,
        String typeggUsername,
        Double wpm,
        Double accuracy,
        Double raw,
        Double pp
) {
}
