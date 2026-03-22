package com.typingstatsvit.api.dto.monkeytype;

public record MonkeytypeScore(
        Double wpm,
        Double acc,
        Double raw,
        Double consistency,
        Long timestamp,
        String language,
        Boolean punctuation,
        Boolean numbers
) {
}
