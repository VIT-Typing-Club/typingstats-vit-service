package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.dto.LeaderboardEntry;
import com.typingstatsvit.api.dto.UserRankProjection;
import com.typingstatsvit.api.entity.Score;
import com.typingstatsvit.api.entity.TestType;
import com.typingstatsvit.api.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {

    // query dynamically checks if the parameters are null.
    // If testType is provided, it filters by it. If null, it ignores it.
    @Query("""
            SELECT new com.typingstatsvit.api.dto.LeaderboardEntry(
                s.user.discordId, s.user.displayName, s.user.username, s.user.avatarUrl, 
                s.wpm, s.accuracy, s.raw, s.testType, s.createdAt
            ) 
            FROM Score s 
            WHERE (:testType IS NULL OR s.testType = :testType) 
            AND (:userId IS NULL OR s.user.discordId = :userId)
            AND s.user.collegeVerified = true
            AND s.user.mtVerified = true
            """)
    List<LeaderboardEntry> getCustomLeaderboard(TestType testType, String userId, Pageable pageable);

    Optional<Score> findByUserAndTestType(User user, TestType testType);

    @Query(value = """
            SELECT test_type AS testType, user_rank AS userRank
            FROM (
                SELECT test_type, user_id, RANK() OVER (PARTITION BY test_type ORDER BY wpm DESC) as user_rank
                FROM scores
            ) ranked_scores
            WHERE user_id = :userId
            """, nativeQuery = true)
    List<UserRankProjection> getUserRanks(@Param("userId") String userId);
}
