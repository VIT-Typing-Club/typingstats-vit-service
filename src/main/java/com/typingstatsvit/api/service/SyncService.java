package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.monkeytype.MonkeytypeProfileResponse;
import com.typingstatsvit.api.dto.monkeytype.MonkeytypeScore;
import com.typingstatsvit.api.entity.Score;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.ScoreRepository;
import com.typingstatsvit.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final MonkeytypeClient monkeytypeClient;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public SyncService(MonkeytypeClient monkeytypeClient, ScoreRepository scoreRepository,
                       UserRepository userRepository, CacheManager cacheManager) {
        this.monkeytypeClient = monkeytypeClient;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public void performManualSync(User user) {
        if (!user.getCollegeVerified() || !user.getMtVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account must be fully verified before syncing scores.");
        }

        if (user.getLastManualSync() != null &&
                user.getLastManualSync().plus(5, ChronoUnit.MINUTES).isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Refresh on cooldown. Please wait 5 minutes.");
        }

        boolean dataChanged = fetchAndUpdateAllScores(user);

        user.setLastManualSync(Instant.now());
        userRepository.save(user);
        log.info("Manual Sync successful user:{} ", user.getDiscordId());

        evictCacheIfChanged(dataChanged);
    }

    @Scheduled(fixedDelay = 600000)
    @Transactional
    public void performAutoSyncTrickle() {
        List<User> staleUsers = userRepository.findTop5ByMtVerifiedTrueAndCollegeVerifiedTrueOrderByLastAutoSyncAsc();
        boolean anyDataChanged = false;

        log.info("Starting auto-sync trickle for {} users...", staleUsers.size());
        for (User user : staleUsers) {
            try {
                if (fetchAndUpdateAllScores(user)) {
                    anyDataChanged = true;
                }
                user.setLastAutoSync(Instant.now());
                userRepository.save(user);
                log.info("Successfully updated scores for user: {}", user.getDiscordId());
            } catch (Exception e) {
                log.error("Auto-sync failed for user: {} - Reason: {}", user.getDiscordId(), e.getMessage());
            }
        }

        evictCacheIfChanged(anyDataChanged);
    }

    private boolean fetchAndUpdateAllScores(User user) {
        String mtUsername = user.getMtUrl();
        if (mtUsername == null) return false;

        MonkeytypeProfileResponse response = monkeytypeClient.getUserProfile(mtUsername);

        if (!isValidMonkeytypeResponse(response)) {
            return false;
        }

        Map<String, List<MonkeytypeScore>> timeBests = response.data().personalBests().time();
        boolean anyUpdates = false;

        anyUpdates |= processTimeNode(user, timeBests, "15", TestType.TIME_15);
        anyUpdates |= processTimeNode(user, timeBests, "30", TestType.TIME_30);
        anyUpdates |= processTimeNode(user, timeBests, "60", TestType.TIME_60);

        return anyUpdates;
    }

    private boolean processTimeNode(User user, Map<String, List<MonkeytypeScore>> timeBests, String timeKey, TestType testType) {
        List<MonkeytypeScore> scoreArray = timeBests.get(timeKey);
        MonkeytypeScore validPb = extractStrictEnglishPb(scoreArray);

        if (validPb != null) {
            return saveOrUpdateScore(user, testType, validPb);
        }
        return false;
    }

    private MonkeytypeScore extractStrictEnglishPb(List<MonkeytypeScore> scores) {
        if (scores == null || scores.isEmpty()) return null;

        return scores.stream()
                .filter(score -> "english".equals(score.language()))
                .filter(score -> !Boolean.TRUE.equals(score.punctuation()))
                .filter(score -> !Boolean.TRUE.equals(score.numbers()))
                .max(Comparator.comparing(MonkeytypeScore::wpm))
                .orElse(null);
    }

    private boolean saveOrUpdateScore(User user, TestType testType, MonkeytypeScore newMtScore) {
        Optional<Score> existingOpt = scoreRepository.findByUserAndTestType(user, testType);

        if (existingOpt.isPresent()) {
            Score existing = existingOpt.get();
            if (existing.getWpm() < newMtScore.wpm() || !existing.getWpm().equals(newMtScore.wpm())) {
                existing.setWpm(newMtScore.wpm());
                existing.setAccuracy(newMtScore.acc());
                existing.setRaw(newMtScore.raw());
                existing.setConsistency(newMtScore.consistency());
                scoreRepository.save(existing);
                return true;
            }
            return false;
        } else {
            Score newScore = new Score();
            newScore.setId(UUID.randomUUID().toString());
            newScore.setUser(user);
            newScore.setTestType(testType);
            newScore.setWpm(newMtScore.wpm());
            newScore.setAccuracy(newMtScore.acc());
            newScore.setRaw(newMtScore.raw());
            newScore.setConsistency(newMtScore.consistency());
            scoreRepository.save(newScore);
            return true;
        }
    }

    private void evictCacheIfChanged(boolean dataChanged) {
        if (dataChanged && cacheManager.getCache("leaderboard") != null) {
            cacheManager.getCache("leaderboard").clear();
        }
    }

    private boolean isValidMonkeytypeResponse(MonkeytypeProfileResponse response) {
        return response != null
                && response.data() != null
                && response.data().personalBests() != null
                && response.data().personalBests().time() != null;
    }
}
