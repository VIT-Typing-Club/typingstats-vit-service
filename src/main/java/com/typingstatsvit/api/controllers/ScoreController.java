package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.DailyLeaderboardResponse;
import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.service.ScoreService;
import com.typingstatsvit.api.service.TypeggSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;
    private final TypeggSyncService typeggSyncService;

    public ScoreController(ScoreService scoreService, TypeggSyncService typeggSyncService) {
        this.scoreService = scoreService;
        this.typeggSyncService = typeggSyncService;
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

    @GetMapping("/typegg/daily")
    public ResponseEntity<DailyLeaderboardResponse> getDailyTypeggCompetition() {
        DailyLeaderboardResponse response = typeggSyncService.getDailyLeaderboard();

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/typegg/sync/{discordId}")
    public ResponseEntity<Map<String, String>> syncTypeggScoresPublic(
            @PathVariable String discordId) {

        typeggSyncService.performPublicSync(discordId);

        return ResponseEntity.ok(Map.of(
                "message", "TypeGG daily score successfully synced"
        ));
    }
}
