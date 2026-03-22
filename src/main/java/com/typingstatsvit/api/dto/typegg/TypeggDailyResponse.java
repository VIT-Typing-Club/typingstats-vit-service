package com.typingstatsvit.api.dto.typegg;

import java.time.Instant;

public record TypeggDailyResponse(
        Instant startDate,
        Instant endDate,
        TypeggQuote quote
) {
}
