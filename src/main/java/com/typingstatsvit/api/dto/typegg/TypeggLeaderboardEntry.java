package com.typingstatsvit.api.dto.typegg;

public record TypeggLeaderboardEntry(
        String discordId,
        String discordUsername,
        String typeggUsername,
        Double wpm,
        Double accuracy,
        Double raw,
        Double pp
) {
}
