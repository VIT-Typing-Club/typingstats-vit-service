package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.typegg.TypeggDailyResponse;
import com.typingstatsvit.api.dto.typegg.TypeggProfileResponse;
import com.typingstatsvit.api.dto.typegg.TypeggRaceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class TypeggClient {

    private final RestClient restClient;

    public TypeggClient(@Value("${application.typegg.base-url:}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public TypeggProfileResponse getUserProfile(String username) {
        return restClient.get()
                .uri("/v1/users/{username}", username)
                .retrieve()
                .body(TypeggProfileResponse.class);
    }

    public TypeggDailyResponse getDailyQuote() {
        String today = LocalDate.now(ZoneOffset.UTC).toString();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/daily")
                        .queryParam("date", today)
                        .build())
                .retrieve()
                .body(TypeggDailyResponse.class);
    }

    public TypeggRaceResponse getBestRaceForQuote(String userId, String quoteId, String startDate, String endDate) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/users/{userId}/races")
                        .queryParam("quoteId", quoteId)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("sort", "wpm")
                        .queryParam("reverse", "true")
                        .queryParam("perPage", "1")
                        .queryParam("page", "1")
                        .build(userId))
                .retrieve()
                .body(TypeggRaceResponse.class);
    }
}
