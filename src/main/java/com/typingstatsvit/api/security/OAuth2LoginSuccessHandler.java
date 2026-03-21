package com.typingstatsvit.api.security;

import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import com.typingstatsvit.api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String discordId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("username");
        String avatarHash = oAuth2User.getAttribute("avatar");

        String avatarUrl = null;
        if (avatarHash != null) {
            avatarUrl = String.format("https://cdn.discordapp.com/avatars/%s/%s.png", discordId, avatarHash);
        }

        User user = userRepository.findById(discordId).orElse(new User());
        user.setDiscordId(discordId);
        user.setUsername(username);
        user.setAvatarUrl(avatarUrl);

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth-callback")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}