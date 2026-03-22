package com.typingstatsvit.api.dto.monkeytype;

import java.util.List;
import java.util.Map;

public record PersonalBests(
        Map<String, List<MonkeytypeScore>> time,
        Map<String, List<MonkeytypeScore>> words
) {
}
