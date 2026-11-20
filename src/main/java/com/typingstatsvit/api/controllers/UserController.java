package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.PublicProfileDto;
import com.typingstatsvit.api.dto.UserRankProjection;
import com.typingstatsvit.api.dto.UserUpdateRequest;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.ScoreRepository;
import com.typingstatsvit.api.repository.UserRepository;
import com.typingstatsvit.api.service.SyncService;
import com.typingstatsvit.api.service.TypeggSyncService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    private final SyncService syncService;
    private final TypeggSyncService typeggSyncService;

    public UserController(UserRepository userRepository, SyncService syncService, ScoreRepository scoreRepository, TypeggSyncService typeggSyncService) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.syncService = syncService;
        this.typeggSyncService = typeggSyncService;
    }

    private String normalize(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @GetMapping("/@me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @CacheEvict(value = "leaderboard", allEntries = true)
    @PatchMapping("/@me")
    public ResponseEntity<User> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserUpdateRequest updateRequest
    ) {
        if (updateRequest.displayName() != null) {
            currentUser.setDisplayName(
                    normalize(updateRequest.displayName())
            );
        }

        if (updateRequest.collegeEmail() != null) {
            String value = normalize(updateRequest.collegeEmail());

            if (!Objects.equals(value, currentUser.getCollegeEmail())) {
                currentUser.setCollegeEmail(value);
                currentUser.setCollegeVerified(false);
            }
        }

        if (updateRequest.mtUrl() != null) {
            String value = normalize(updateRequest.mtUrl());

            if (!Objects.equals(value, currentUser.getMtUrl())) {
                currentUser.setMtUrl(value);
                currentUser.setMtVerified(false);
            }
        }

        if (updateRequest.linkedinUrl() != null) {
            currentUser.setLinkedinUrl(normalize(updateRequest.linkedinUrl()));
        }

        if (updateRequest.githubUrl() != null) {
            currentUser.setGithubUrl(normalize(updateRequest.githubUrl()));
        }

        if (updateRequest.instagramUrl() != null) {
            currentUser.setInstagramUrl(normalize(updateRequest.instagramUrl()));
        }

        if (updateRequest.xUrl() != null) {
            currentUser.setXUrl(normalize(updateRequest.xUrl()));
        }

        User savedUser = userRepository.save(currentUser);

        return ResponseEntity.ok(savedUser);
    }

    @CacheEvict(value = "leaderboard", allEntries = true)
    @DeleteMapping("/@me")
    public ResponseEntity<Map<String, String>> deleteAccount(@AuthenticationPrincipal User currentUser) {
        userRepository.deleteById(currentUser.getDiscordId());
        return ResponseEntity.ok(Map.of("Message", "Deleted User successfully."));
    }

    @PostMapping("/@me/sync")
    public ResponseEntity<Map<String, String>> syncScores(@AuthenticationPrincipal User currentUser) {
        syncService.performManualSync(currentUser);
        return ResponseEntity.ok(Map.of("message", "Scores successfully synced with Monkeytype"));
    }

    @GetMapping("/@me/ranks")
    public ResponseEntity<Map<String, Long>> getCurrentUserRanks(@AuthenticationPrincipal User currentUser) {

        List<UserRankProjection> projections = scoreRepository.getUserRanks(currentUser.getDiscordId());

        Map<String, Long> ranks = projections.stream()
                .collect(Collectors.toMap(
                        UserRankProjection::getTestType,
                        UserRankProjection::getUserRank
                ));

        return ResponseEntity.ok(ranks);
    }

    @PostMapping("/@me/typegg/sync")
    public ResponseEntity<Map<String, String>> syncTypeggScores(@AuthenticationPrincipal User currentUser) {
        typeggSyncService.performManualSync(currentUser);
        return ResponseEntity.ok(Map.of("message", "TypeGG daily score successfully synced"));
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicProfileDto> getPublicProfile(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> new PublicProfileDto(
                        user.getDiscordId(),
                        user.getDisplayName(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        user.getMtUrl(),
                        user.getMtVerified(),
                        user.getCollegeVerified(),
                        user.getTypeggUsername(),
                        user.getLinkedinUrl(),
                        user.getGithubUrl(),
                        user.getInstagramUrl(),
                        user.getXUrl()
                ))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
