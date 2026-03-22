package com.typingstatsvit.api.dto.monkeytype;

public record MonkeytypeData(
        String name,
        PersonalBests personalBests,
        MonkeytypeDetails details
) {
}
