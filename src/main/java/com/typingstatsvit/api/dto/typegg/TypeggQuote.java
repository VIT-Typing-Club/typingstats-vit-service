package com.typingstatsvit.api.dto.typegg;

public record TypeggQuote(
        String quoteId,
        String text,
        Double difficulty,
        TypeggSource source
) {
}
