package com.typingstatsvit.api.dto.typegg;

import java.util.List;

public record TypeggProfileResponse(
        String userId,
        String username,
        String displayName,
        List<LinkedAccount> linkedAccounts
) {
    public record LinkedAccount(
            String platform,
            String platformUserId,
            Boolean isVerified,
            Integer displayOrder
    ) {
    }
}
