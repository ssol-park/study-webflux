package com.webflux.study.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientUtil {

    private final WebClient webClient;

    public WebClientUtil(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<ServerResponse<T>> postUsingRetrieve(String url, MultiValueMap<String, String> requestHeader, Object requestBody, Class<T> responseType) {
        log.info("[PostUsingRetrieve] request to URL: {} with headers: {} and body: {}", url, requestHeader, requestBody);

        return webClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(requestHeader))
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType)
                .flatMap(data -> buildServerResponse(HttpStatus.OK,"success", data))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error request: {}", ex.getMessage());
                    return buildServerResponse(HttpStatus.resolve(ex.getStatusCode().value()), ex.getResponseBodyAsString(), null);
                });
    }

    public <T> Mono<ServerResponse<T>> postUsingExchange(String url, MultiValueMap<String, String> requestHeader, Object requestBody, Class<T> responseType) {
        log.info("[PostUsingExchange] request to URL: {} with headers: {} and body: {}", url, requestHeader, requestBody);

        return webClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(requestHeader))
                .body(BodyInserters.fromValue(requestBody))
                .exchangeToMono(response -> response.bodyToMono(responseType))
                .flatMap(data -> buildServerResponse(HttpStatus.OK, "success", data))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error during POST request: {}", ex.getMessage());
                    return buildServerResponse(HttpStatus.OK, "Error: " + ex.getMessage(), null);
                })
                .onErrorResume(Throwable.class, ex -> {
                    log.error("Unexpected error during POST request: {}", ex.getMessage());
                    return buildServerResponse(HttpStatus.OK, "Unexpected error: " + ex.getMessage(), null);
                });
    }

    private <T> Mono<ServerResponse<T>> buildServerResponse(HttpStatus status, String message, T data) {
        return Mono.just(ServerResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build());
    }
}
