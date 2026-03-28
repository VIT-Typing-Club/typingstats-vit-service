package com.typingstatsvit.api.repository;

import com.typingstatsvit.api.entity.DailyQuote;
import com.typingstatsvit.api.entity.TypeggScore;
import com.typingstatsvit.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TypeggScoreRepository extends JpaRepository<TypeggScore, String> {
    Optional<TypeggScore> findByUserAndQuote(User user, DailyQuote quote);

    List<TypeggScore> findByQuoteOrderByWpmDesc(DailyQuote quote);
}
