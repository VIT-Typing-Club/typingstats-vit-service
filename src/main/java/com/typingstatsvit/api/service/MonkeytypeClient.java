package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.monkeytype.MonkeytypeProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MonkeytypeClient {

    private final RestClient restClient;

    public MonkeytypeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public MonkeytypeProfileResponse getUserProfile(String username) {
        return restClient.get()
                .uri("/users/{username}/profile", username)
                .retrieve()
                .body(MonkeytypeProfileResponse.class);
    }
}
