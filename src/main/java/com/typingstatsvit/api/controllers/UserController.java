package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.UserUpdateRequest;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/@me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @PatchMapping("/@me")
    public ResponseEntity<User> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserUpdateRequest updateRequest
    ) {
        if (updateRequest.displayName() != null) {
            currentUser.setDisplayName(updateRequest.displayName());
        }

        if (updateRequest.collegeEmail() != null && !updateRequest.collegeEmail().equals(currentUser.getCollegeEmail())) {
            currentUser.setCollegeEmail(updateRequest.collegeEmail());
            currentUser.setCollegeVerified(false);
        }

        if (updateRequest.mtUrl() != null && !updateRequest.mtUrl().equals(currentUser.getMtUrl())) {
            currentUser.setMtUrl(updateRequest.mtUrl());
            currentUser.setMtVerified(false);
        }

        if (updateRequest.linkedinUrl() != null) {
            currentUser.setLinkedinUrl(updateRequest.linkedinUrl());
        }

        if (updateRequest.githubUrl() != null) {
            currentUser.setGithubUrl(updateRequest.githubUrl());
        }

        User savedUser = userRepository.save(currentUser);
        return ResponseEntity.ok(savedUser);
    }
}
