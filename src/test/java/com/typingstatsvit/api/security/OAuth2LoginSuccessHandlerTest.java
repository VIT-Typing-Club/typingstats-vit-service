package com.typingstatsvit.api.security;

import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OAuth2LoginSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void shouldCreateNewUserAndRedirectWithToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("id")).thenReturn("999888777");
        when(oAuth2User.getAttribute("username")).thenReturn("new_typer");
        when(oAuth2User.getAttribute("avatar")).thenReturn("abc123hash");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);

        when(userRepository.findById("999888777")).thenReturn(Optional.empty()); // User does not exist yet
        when(jwtService.generateToken(any(User.class))).thenReturn("fake.jwt.token");

        successHandler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getDiscordId()).isEqualTo("999888777");
        assertThat(savedUser.getUsername()).isEqualTo("new_typer");
        assertThat(savedUser.getAvatarUrl()).isEqualTo("https://cdn.discordapp.com/avatars/999888777/abc123hash.png");

        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:3000/auth-callback?token=fake.jwt.token");
    }

    @Test
    void shouldUpdateExistingUserAndRedirectWithToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("id")).thenReturn("999888777");
        when(oAuth2User.getAttribute("username")).thenReturn("updated_name");
        when(oAuth2User.getAttribute("avatar")).thenReturn("newhash");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);

        User existingUser = new User();
        existingUser.setDiscordId("999888777");

        when(userRepository.findById("999888777"))
                .thenReturn(Optional.of(existingUser));

        when(jwtService.generateToken(existingUser))
                .thenReturn("fake.jwt.token");

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepository).save(existingUser);

        assertThat(existingUser.getUsername()).isEqualTo("updated_name");
        assertThat(existingUser.getAvatarUrl())
                .isEqualTo("https://cdn.discordapp.com/avatars/999888777/newhash.png");

        assertThat(response.getRedirectedUrl())
                .isEqualTo("http://localhost:3000/auth-callback?token=fake.jwt.token");
    }
}
