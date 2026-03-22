package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.dto.ConfirmOtpRequest;
import com.typingstatsvit.api.dto.SendOtpRequest;
import com.typingstatsvit.api.dto.monkeytype.MtVerifyRequest;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/monkeytype")
    public ResponseEntity<Map<String, String>> verifyMonkeytype(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody MtVerifyRequest request
    ) {
        verificationService.verifyMonkeytypeProfile(currentUser, request.username());

        return ResponseEntity.ok(Map.of("message", "Monkeytype verified successfully"));
    }

    @PostMapping("/college/send")
    public ResponseEntity<Map<String, String>> sendCollegeOtp(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SendOtpRequest request
    ) {
        verificationService.sendCollegeEmailOtp(currentUser, request.email());
        return ResponseEntity.ok(Map.of("message", "Verification code sent"));
    }

    @PostMapping("/college/confirm")
    public ResponseEntity<Map<String, String>> confirmCollegeOtp(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ConfirmOtpRequest request
    ) {
        verificationService.confirmCollegeEmailOtp(currentUser, request.code());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }
}
