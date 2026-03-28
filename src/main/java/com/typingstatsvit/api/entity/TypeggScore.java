package com.typingstatsvit.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "typegg_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "quote_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeggScore {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private DailyQuote quote;

    @Column(nullable = false)
    private Double wpm;

    private Double accuracy;
    private Double raw;
    private Double pp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
