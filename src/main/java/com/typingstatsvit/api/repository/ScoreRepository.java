package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.entity.Score;
import com.typingstatsvit.api.entity.TestType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {

    // query dynamically checks if the parameters are null.
    // If testType is provided, it filters by it. If null, it ignores it.
    @Query("""
            SELECT new com.typingstatsvit.api.dto.LeaderboardEntry(
                s.user.discordId, s.user.username, s.user.avatarUrl, 
                s.wpm, s.accuracy, s.raw, s.testType, s.createdAt
            ) 
            FROM Score s 
            WHERE (:testType IS NULL OR s.testType = :testType) 
            AND (:userId IS NULL OR s.user.discordId = :userId)
            """)
    List<LeaderboardEntry> getCustomLeaderboard(TestType testType, String userId, Pageable pageable);
}
