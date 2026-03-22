package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.monkeytype.MonkeytypeProfileResponse;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;

@Service
public class VerificationService {

    private final MonkeytypeClient monkeytypeClient;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public VerificationService(MonkeytypeClient monkeytypeClient, UserRepository userRepository, JavaMailSender mailSender) {
        this.monkeytypeClient = monkeytypeClient;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void verifyMonkeytypeProfile(User user, String mtUsername) {
        MonkeytypeProfileResponse response;

        try {
            response = monkeytypeClient.getUserProfile(mtUsername);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to connect to Monkeytype API");
        }

        if (response == null || response.data() == null ||
                response.data().details() == null || response.data().details().bio() == null) {

            revokeVerification(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No bio found on this Monkeytype profile.");
        }

        String bio = response.data().details().bio();

        if (!bio.contains("[VIT]")) {
            revokeVerification(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification tag '[VIT]' not found in bio.");
        }

        user.setMtUrl(mtUsername);
        user.setMtVerified(true);
        userRepository.save(user);
    }

    private void revokeVerification(User user) {
        user.setMtUrl(null);
        user.setMtVerified(false);
        userRepository.save(user);
    }

    @Transactional
    public void sendCollegeEmailOtp(User user, String email) {
        SecureRandom random = new SecureRandom();
        String code = String.format("%06d", random.nextInt(999999));

        user.setCollegeEmail(email);
        user.setCollegeCode(code);
        user.setCollegeVerified(false);
        userRepository.save(user);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("VIT Typing Stats - Email Verification");
            message.setText("Your verification code is: " + code + "\n\nThis code will be used to verify your student status.");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send verification email. Please try again.");
        }
    }

    @Transactional
    public void confirmCollegeEmailOtp(User user, String code) {
        if (user.getCollegeCode() == null || !user.getCollegeCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code.");
        }
        user.setCollegeVerified(true);
        user.setCollegeCode(null);
        userRepository.save(user);
    }
}