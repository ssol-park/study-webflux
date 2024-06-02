package com.webflux.study.webfilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class WebFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("리액티브 웹 필터 테스트")
    void webFilterTest() {
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/posts")
                                .queryParam("user", "psr")
                                .build())
                .exchange()
                .expectStatus().isOk()
                ;
    }
}
