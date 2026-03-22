package com.typingstatsvit.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "discord_id", nullable = false)
    private String discordId;

    @Column(nullable = false)
    private String username;

    @Column(name = "displayname")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "college_email")
    private String collegeEmail;

    @Column(name = "college_verified", nullable = false)
    private Boolean collegeVerified = false;

    @Column(name = "college_code")
    private String collegeCode;

    @Column(name = "mt_verified", nullable = false)
    private Boolean mtVerified = false;

    @Column(name = "mt_url")
    private String mtUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "x_url")
    private String xUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_manual_sync")
    private Instant lastManualSync;

    @Column(name = "last_auto_sync")
    private Instant lastAutoSync;
}
