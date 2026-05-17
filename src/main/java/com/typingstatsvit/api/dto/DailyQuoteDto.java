package com.typingstatsvit.api.dto;

import java.time.Instant;

public record DailyQuoteDto(
        String quoteId,
        String text,
        String sourceTitle,
        Double difficulty,
        Instant endDate
) {
}
