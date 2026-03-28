package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.entity.DailyQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface DailyQuoteRepository extends JpaRepository<DailyQuote, String> {
    @Query("SELECT d FROM DailyQuote d WHERE :now BETWEEN d.startDate AND d.endDate")
    Optional<DailyQuote> findActiveQuote(@Param("now") Instant now);
}
