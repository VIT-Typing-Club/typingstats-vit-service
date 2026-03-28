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

@Entity
@Table(name = "daily_quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuote {

    @Id
    @Column(name = "quote_id", nullable = false)
    private String quoteId;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "source_title")
    private String sourceTitle;

    private Double difficulty;
}
