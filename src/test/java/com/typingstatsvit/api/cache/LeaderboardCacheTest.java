package com.typingstatsvit.api.cache;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.Score;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.ScoreRepository;
import com.typingstatsvit.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LeaderboardCacheTest {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        scoreRepository.deleteAll();
        userRepository.deleteAll();

        if (cacheManager.getCache("leaderboard") != null) {
            cacheManager.getCache("leaderboard").clear();
        }

        testUser = new User();
        testUser.setDiscordId("cache_tester_99");
        testUser.setUsername("cache_god");
        userRepository.save(testUser);
    }

    @Disabled("check null issue with hibernate")
    @Test
    void shouldReturnCachedDataEvenIfDatabaseChanges() {

        Score initialScore = new Score();
        initialScore.setId(UUID.randomUUID().toString());
        initialScore.setUser(testUser);
        initialScore.setTestType(TestType.TIME_60);
        initialScore.setWpm(100.0);
        initialScore.setCreatedAt(Instant.now());
        scoreRepository.save(initialScore);

        PageRequest page = PageRequest.of(0, 10);

        // hit db, as cache miss (first time qeury)
        List<LeaderboardEntry> firstFetch = scoreRepository.getCustomLeaderboard(TestType.TIME_60, null, page);

        assertThat(firstFetch).hasSize(1);
        assertThat(firstFetch.get(0).wpm()).isEqualTo(100.0);

        //update db
        initialScore.setWpm(150.0);
        scoreRepository.save(initialScore);

        // fetch the leaderboard again using the same parameters
        List<LeaderboardEntry> secondFetch = scoreRepository.getCustomLeaderboard(TestType.TIME_60, null, page);

        assertThat(secondFetch.get(0).wpm()).isEqualTo(100.0);// should be 100 because cache hasn't updated yet

        cacheManager.getCache("leaderboard").clear();

        List<LeaderboardEntry> thirdFetch = scoreRepository.getCustomLeaderboard(TestType.TIME_60, null, page);

        assertThat(thirdFetch.get(0).wpm()).isEqualTo(150.0);
    }
}
