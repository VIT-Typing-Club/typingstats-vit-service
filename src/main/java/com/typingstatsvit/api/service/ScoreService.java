package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.repository.ScoreRepository;
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

    public List<LeaderboardEntry> getLeaderboard(TestType testType, String userId, int safeLimit) {
        PageRequest pageRequest = PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "wpm"));
        return this.scoreRepository.getCustomLeaderboard(testType, userId, pageRequest);
    }
}
