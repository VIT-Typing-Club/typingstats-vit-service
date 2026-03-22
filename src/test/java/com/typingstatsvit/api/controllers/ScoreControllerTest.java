package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.repository.ScoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoreRepository scoreRepository;

    @Test
    void shouldReturnOverallLeaderboardWithDefaultLimit() throws Exception {
        LeaderboardEntry entry1 = new LeaderboardEntry(
                "111", "speed_demon", "url1", 160.5, 99.0, 162.0, TestType.TIME_60, Instant.now()
        );
        LeaderboardEntry entry2 = new LeaderboardEntry(
                "222", "slow_poke", "url2", 45.0, 90.0, 45.0, TestType.TIME_60, Instant.now()
        );

        when(scoreRepository.getCustomLeaderboard(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(List.of(entry1, entry2));

        mockMvc.perform(get("/api/scores/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("speed_demon"))
                .andExpect(jsonPath("$[0].wpm").value(160.5))
                .andExpect(jsonPath("$[1].username").value("slow_poke"));
    }

    @Test
    void shouldFilterLeaderboardByTestType() throws Exception {
        LeaderboardEntry entry = new LeaderboardEntry(
                "333", "words_master", "url3", 120.0, 100.0, 120.0, TestType.WORDS_10, Instant.now()
        );

        when(scoreRepository.getCustomLeaderboard(eq(TestType.WORDS_10), isNull(), any(Pageable.class)))
                .thenReturn(List.of(entry));

        mockMvc.perform(get("/api/scores/leaderboard")
                        .param("testType", "WORDS_10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].testType").value("WORDS_10"))
                .andExpect(jsonPath("$[0].username").value("words_master"));
    }

    @Test
    void shouldFilterLeaderboardByUserId() throws Exception {
        LeaderboardEntry entry = new LeaderboardEntry(
                "999", "just_me", "url4", 100.0, 95.0, 105.0, TestType.TIME_15, Instant.now()
        );

        when(scoreRepository.getCustomLeaderboard(isNull(), eq("999"), any(Pageable.class)))
                .thenReturn(List.of(entry));

        mockMvc.perform(get("/api/scores/leaderboard")
                        .param("userId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].discordId").value("999"));
    }
}
