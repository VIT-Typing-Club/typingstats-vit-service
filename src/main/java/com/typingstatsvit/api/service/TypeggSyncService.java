package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.DailyLeaderboardResponse;
import com.typingstatsvit.api.dto.DailyQuoteDto;
import com.typingstatsvit.api.dto.typegg.TypeggDailyResponse;
import com.typingstatsvit.api.dto.typegg.TypeggLeaderboardEntry;
import com.typingstatsvit.api.dto.typegg.TypeggRace;
import com.typingstatsvit.api.dto.typegg.TypeggRaceResponse;
import com.typingstatsvit.api.entity.DailyQuote;
import com.typingstatsvit.api.entity.TypeggScore;
import com.typingstatsvit.api.entity.User;
import com.typingstatsvit.api.repository.DailyQuoteRepository;
import com.typingstatsvit.api.repository.TypeggScoreRepository;
import com.typingstatsvit.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeggSyncService {

    private static final Logger log = LoggerFactory.getLogger(TypeggSyncService.class);

    private final TypeggClient typeggClient;
    private final DailyQuoteRepository dailyQuoteRepository;
    private final TypeggScoreRepository typeggScoreRepository;
    private final UserRepository userRepository;

    public TypeggSyncService(TypeggClient typeggClient, DailyQuoteRepository dailyQuoteRepository,
                             TypeggScoreRepository typeggScoreRepository, UserRepository userRepository) {
        this.typeggClient = typeggClient;
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.typeggScoreRepository = typeggScoreRepository;
        this.userRepository = userRepository;
    }

    /**
     * scheduled jobs:
     * fetch and save daily quote at UTC midnight + 5 mins
     * update daily quote leaderboard every x mins
     */
    @Scheduled(cron = "0 5 0 * * *", zone = "UTC")
    @Transactional
    public void scheduledFetchDailyQuote() {
        try {
            forceFetchAndSaveDailyQuote();
        } catch (Exception e) {
            log.error("Scheduled Daily Quote fetch failed: {}", e.getMessage());
        }
    }

    @Transactional
    public DailyQuote forceFetchAndSaveDailyQuote() {
        log.info("Fetching new TypeGG Daily Quote...");

        TypeggDailyResponse response = typeggClient.getDailyQuote();

        if (response == null || response.quote() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TypeGG returned an empty quote response.");
        }

        DailyQuote quote = new DailyQuote();
        quote.setQuoteId(response.quote().quoteId());
        quote.setStartDate(response.startDate());
        quote.setEndDate(response.endDate());
        quote.setText(response.quote().text());
        quote.setDifficulty(response.quote().difficulty());

        if (response.quote().source() != null) {
            quote.setSourceTitle(response.quote().source().title());
        }

        DailyQuote savedQuote = dailyQuoteRepository.save(quote);
        log.info("Successfully saved new Daily Quote: {}", savedQuote.getQuoteId());

        return savedQuote;
    }

    @Scheduled(fixedDelay = 600000)
    @Transactional
    public void performAutoSyncTrickle() {
        Optional<DailyQuote> activeQuoteOpt = dailyQuoteRepository.findActiveQuote(Instant.now());
        if (activeQuoteOpt.isEmpty()) return;

        DailyQuote activeQuote = activeQuoteOpt.get();
        List<User> staleUsers = userRepository.findTop10ByTypeggIdIsNotNullOrderByLastTypeggAutoSyncAsc();

        for (User user : staleUsers) {
            log.info("Checking user {}", user.getDiscordId());
            if (user.getLastTypeggAutoSync() != null &&
                    user.getLastTypeggAutoSync().plus(1, ChronoUnit.HOURS).isAfter(Instant.now())) {
                continue;
            }

            try {
                syncUserForQuote(user, activeQuote);
                user.setLastTypeggAutoSync(Instant.now());
                userRepository.save(user);
            } catch (Exception e) {
                log.error("Failed TypeGG sync for user {}: {}", user.getDiscordId(), e.getMessage());
            }
        }
    }

    /**
     * manual update daily quote score
     *
     * @param user
     */
    @Transactional
    public void performManualSync(User user) {
        if (user.getTypeggId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TypeGG account not linked.");
        }

        if (user.getLastTypeggManualSync() != null &&
                user.getLastTypeggManualSync().plus(1, ChronoUnit.MINUTES).isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Sync is on cooldown. Please wait 1 minute between manual refreshes.");
        }

        DailyQuote activeQuote = dailyQuoteRepository.findActiveQuote(Instant.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active daily quote found."));

        boolean foundScore = syncUserForQuote(user, activeQuote);

        user.setLastTypeggManualSync(Instant.now());
        userRepository.save(user);

        if (!foundScore) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No races found for this quote. Have you played today's daily yet?");
        }
    }

    @Transactional(readOnly = true)
    public DailyLeaderboardResponse getDailyLeaderboard() {
        Optional<DailyQuote> activeQuoteOpt = dailyQuoteRepository.findActiveQuote(Instant.now());

        if (activeQuoteOpt.isEmpty()) {
            return null;
        }

        DailyQuote activeQuote = activeQuoteOpt.get();
        List<TypeggScore> scores = typeggScoreRepository.findByQuoteOrderByWpmDesc(activeQuote);

        DailyQuoteDto quoteDto = new DailyQuoteDto(
                activeQuote.getText(),
                activeQuote.getSourceTitle(),
                activeQuote.getDifficulty(),
                activeQuote.getEndDate()
        );

        List<TypeggLeaderboardEntry> leaderboard = scores.stream()
                .map(score -> new TypeggLeaderboardEntry(
                        score.getUser().getDiscordId(),
                        score.getUser().getUsername(),
                        score.getUser().getTypeggUsername(),
                        score.getWpm(),
                        score.getAccuracy(),
                        score.getRaw(),
                        score.getPp()
                ))
                .toList();

        return new DailyLeaderboardResponse(quoteDto, leaderboard);
    }

    private boolean syncUserForQuote(User user, DailyQuote quote) {
        log.info("Requesting best race for User ID: {} on Quote ID: {}", user.getTypeggId(), quote.getQuoteId());

        Instant adjustedStartDate = quote.getStartDate().minus(1, ChronoUnit.DAYS);
        Instant adjustedEndDate = quote.getEndDate().plus(1, ChronoUnit.DAYS);

        TypeggRaceResponse response = typeggClient.getBestRaceForQuote(
                user.getTypeggId(),
                quote.getQuoteId(),
                adjustedStartDate.toString(),
                adjustedEndDate.toString()
        );

        if (response == null || response.races() == null) {
            log.warn("TypeGG API returned a null response or missing races array.");
            return false;
        }

        if (response.races().isEmpty()) {
            log.info("TypeGG API returned 0 races. User has not played this quote today.");
            return false;
        }

        TypeggRace bestRace = response.races().get(0);
        log.info("Found best race! WPM: {}", bestRace.wpm());

        Optional<TypeggScore> existingOpt = typeggScoreRepository.findByUserAndQuote(user, quote);

        if (existingOpt.isPresent()) {
            updateExistingScore(existingOpt.get(), bestRace);
        } else {
            createNewScore(user, quote, bestRace);
        }

        return true;
    }

    private void updateExistingScore(TypeggScore existing, TypeggRace bestRace) {
        if (existing.getWpm() < bestRace.wpm()) {
            existing.setWpm(bestRace.wpm());
            existing.setAccuracy(bestRace.accuracy());
            existing.setRaw(bestRace.raw());
            existing.setPp(bestRace.pp());
            typeggScoreRepository.save(existing);
        }
    }

    private void createNewScore(User user, DailyQuote quote, TypeggRace bestRace) {
        TypeggScore newScore = new TypeggScore();
        newScore.setId(UUID.randomUUID().toString());
        newScore.setUser(user);
        newScore.setQuote(quote);
        newScore.setWpm(bestRace.wpm());
        newScore.setAccuracy(bestRace.accuracy());
        newScore.setRaw(bestRace.raw());
        newScore.setPp(bestRace.pp());
        typeggScoreRepository.save(newScore);
    }
}