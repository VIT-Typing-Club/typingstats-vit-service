package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.typegg.TypeggProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}
