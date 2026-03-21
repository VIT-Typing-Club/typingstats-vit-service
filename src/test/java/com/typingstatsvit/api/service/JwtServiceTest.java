package com.typingstatsvit.api.service;

import com.typingstatsvit.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {
    private JwtService JwtService;
    private User mockUser;

    @BeforeEach
    void setUp() {
        String dummySecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long dummyExpiration = 86400000L;

        jwtService = new JwtService(dummySecret, dummyExpiration);

        mockUser = new User();
        mockUser.setDiscordId("1234567890");
        mockUser.setUsername("test_user");
    }

    @Test
    void shouldGenerateValidTokenAndExtractDiscordId() {
        String token = jwtService.generateToken(mockUser);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);

        String extractedDiscordId = jwtService.extractDiscordId(token);
        assertThat(extractedDiscordId).isEqualTo("1234567890");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }
}
