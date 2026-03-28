package com.typingstatsvit.api.service;

import com.typingstatsvit.api.dto.monkeytype.MonkeytypeProfileResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class MonkeytypeClientTest {

    private MockWebServer mockWebServer;
    private MonkeytypeClient monkeytypeClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        monkeytypeClient = new MonkeytypeClient(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldFetchAndDeserializeUserProfile() {
        String mockJsonResponse = """
                {
                  "message": "Profile retrieved",
                  "data": {
                    "name": "bazooka",
                    "personalBests": {
                      "time": {
                        "15": [
                          { "wpm": 180.5, "acc": 98.2, "raw": 185.0, "timestamp": 1700000000, "consistency":90.0, "language":"english"}
                        ],
                        "60": [
                          { "wpm": 145.0, "acc": 96.5, "raw": 150.0, "timestamp": 1700005000, "consistency":91.0, "language":"english"}
                        ]
                      }
                    }
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockJsonResponse)
                .addHeader("Content-Type", "application/json"));

        MonkeytypeProfileResponse response = monkeytypeClient.getUserProfile("bazooka");

        assertThat(response).isNotNull();
        assertThat(response.data().name()).isEqualTo("bazooka");

        assertThat(response.data().personalBests().time().get("15")).hasSize(1);
        assertThat(response.data().personalBests().time().get("15").get(0).wpm()).isEqualTo(180.5);

        assertThat(response.data().personalBests().time().get("60").get(0).acc()).isEqualTo(96.5);

        assertThat(response.data().personalBests().time().get("30")).isNull();
    }
}
