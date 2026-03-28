package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.repository.ScoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Cacheable(value = "leaderboard", key = "{#testType, #userId, #safeLimit}")
    public List<LeaderboardEntry> getLeaderboard(TestType testType, String userId, int safeLimit) {
        System.out.println(">>> DB QUERY SHOULD RUN NOW");
        PageRequest pageRequest = PageRequest.of(
                0, safeLimit, Sort.by(Sort.Direction.DESC, "wpm")
        );
        return scoreRepository.getCustomLeaderboard(testType, userId, pageRequest);
    }
}
