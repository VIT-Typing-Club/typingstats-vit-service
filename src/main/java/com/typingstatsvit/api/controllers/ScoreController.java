package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam(required = false) TestType testType,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        int safeLimit = Math.min(limit, 100);
        List<LeaderboardEntry> leaderboard = scoreService.getLeaderboard(testType, userId, safeLimit);
        return ResponseEntity.ok(leaderboard);
    }
}
