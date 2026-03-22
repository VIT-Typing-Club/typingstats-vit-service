package com.typingstatsvit.api.dto;

import com.typingstatsvit.api.dto.typegg.TypeggLeaderboardEntry;

import java.util.List;

public record DailyLeaderboardResponse(
        DailyQuoteDto quote,
        List<TypeggLeaderboardEntry> leaderboard
) {
}
