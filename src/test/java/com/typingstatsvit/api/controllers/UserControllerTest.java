package com.typingstatsvit.api.controllers;


import com.typingstatsvit.api.dto.UserUpdateRequest;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import com.typingstatsvit.api.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdatePartialUserProfilePartially() throws Exception {
        User mockUser = new User();
        mockUser.setDiscordId("999888777");
        mockUser.setUsername("kaboom");

        String fakeToken = "valid.cookie.token";

        when(userRepository.findById("999888777")).thenReturn(Optional.of(mockUser));
        when(jwtService.extractDiscordId(fakeToken)).thenReturn("999888777");
        when(jwtService.isTokenValid(fakeToken, mockUser)).thenReturn(true);
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        UserUpdateRequest updatePayload = new UserUpdateRequest(
                "kablow", null, null, null, "kablow"
        );

        Cookie jwtCookie = new Cookie("jwt", fakeToken);
        mockMvc.perform(patch("/api/users/@me")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("kablow"))
                .andExpect(jsonPath("$.githubUrl").value("kablow"))
                .andExpect(jsonPath("$.username").value("kaboom")); // username is not update
    }

    @Test
    void shouldRevokeVerificationWhenSensitiveFieldsChange() throws Exception {
        User mockUser = new User();
        mockUser.setDiscordId("111222333");
        mockUser.setUsername("verified_typer");
        mockUser.setCollegeVerified(true);
        mockUser.setMtVerified(true);
        mockUser.setCollegeEmail("old@vit.ac.in");
        mockUser.setMtUrl("oldmturl");

        String fakeToken = "valid.cookie.token";

        when(userRepository.findById("111222333")).thenReturn(Optional.of(mockUser));
        when(jwtService.extractDiscordId(fakeToken)).thenReturn("111222333");
        when(jwtService.isTokenValid(fakeToken, mockUser)).thenReturn(true);
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        UserUpdateRequest updatePayload = new UserUpdateRequest(
                null, "new@vit.ac.in", "newmt", null, null
        );

        Cookie jwtCookie = new Cookie("jwt", fakeToken);

        mockMvc.perform(patch("/api/users/@me")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collegeEmail").value("new@vit.ac.in"))
                .andExpect(jsonPath("$.collegeVerified").value(false))
                .andExpect(jsonPath("$.mtUrl").value("newmt"))
                .andExpect(jsonPath("$.mtVerified").value(false));
    }

    @Test
    void shouldRejectInvalidUpdatePayloadWith400() throws Exception {
        User mockUser = new User();
        mockUser.setDiscordId("999");

        String fakeToken = "valid.cookie.token";

        when(userRepository.findById("999")).thenReturn(Optional.of(mockUser));
        when(jwtService.extractDiscordId(fakeToken)).thenReturn("999");
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(jwtService.isTokenValid(fakeToken, mockUser)).thenReturn(true);
        Cookie jwtCookie = new Cookie("jwt", fakeToken);

        // 1. Email doesn't end in @vit.ac.in
        // 2. MT URL profile name has spaces
        // 3. GitHub username has spaces (invalid)
        UserUpdateRequest badPayload = new UserUpdateRequest(
                "charliekirk",
                "charliekirk@gmail.com",
                "name with spaces",
                "valid-linkedin-name",
                "name with spaces"
        );

        mockMvc.perform(patch("/api/users/@me")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPayload)))
                .andExpect(status().isBadRequest());
    }
}
