package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("""
            SELECT u FROM User u
            WHERE u.mtVerified = true
              AND u.collegeVerified = true
            ORDER BY
              CASE WHEN u.lastAutoSync IS NULL THEN 0 ELSE 1 END,
              u.lastAutoSync ASC
            """)
    List<User> findUsersForAutoSync(Pageable pageable);

    List<User> findTop10ByTypeggIdIsNotNullOrderByLastTypeggAutoSyncAsc();

    Optional<User> findByUsername(String username);
}