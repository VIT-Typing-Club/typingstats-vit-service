package com.typingstatsvit.api.dto;

public record DailyQuoteDto(
        String text,
        String sourceTitle,
        Double difficulty
) {
}
