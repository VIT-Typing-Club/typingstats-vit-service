package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findTop5ByMtVerifiedTrueAndCollegeVerifiedTrueOrderByLastAutoSyncAsc();

    List<User> findTop10ByTypeggIdIsNotNullOrderByLastTypeggAutoSyncAsc();
}