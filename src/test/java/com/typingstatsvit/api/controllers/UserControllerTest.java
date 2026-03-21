package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import com.typingstatsvit.api.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnCurrentUserProfileWhenCookieIsValid() throws Exception {

        User mockUser = new User();
        mockUser.setDiscordId("999888777");
        mockUser.setUsername("type_god");
        mockUser.setAvatarUrl("https://discord.com/avatar.png");

        String fakeToken = "valid.cookie.token";
        when(jwtService.extractDiscordId(fakeToken)).thenReturn("999888777");
        when(userRepository.findById("999888777")).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(fakeToken, mockUser)).thenReturn(true);

        Cookie jwtCookie = new Cookie("jwt", fakeToken);

        mockMvc.perform(get("/api/users/@me").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discordId").value("999888777"))
                .andExpect(jsonPath("$.username").value("type_god"));
    }

    @Test
    void shouldRejectRequestWhenNoCookieProvided() throws Exception {
        mockMvc.perform(get("/api/users/@me"))
                .andExpect(status().isForbidden());
    }
}
