package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/@me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }
}
