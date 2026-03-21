package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByDiscordId() {
        User newUser = new User();
        newUser.setDiscordId("1234567890");
        newUser.setUsername("bazooka");
        newUser.setAvatarUrl("https://cdn.discordapp.com/avatars/123/abc.png");

        userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById("1234567890");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getDiscordId()).isEqualTo("1234567890");
        assertThat(foundUser.get().getUsername()).isEqualTo("bazooka");
        assertThat(foundUser.get().getAvatarUrl())
                .isEqualTo("https://cdn.discordapp.com/avatars/123/abc.png");
    }
}
