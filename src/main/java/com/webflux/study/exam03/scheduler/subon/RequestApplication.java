package com.webflux.study.exam03.scheduler.subon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class RequestApplication {

    @RestController
    @RequestMapping(("/api"))
    public static class ApiController {
        private final ApiService apiService;

        public ApiController(ApiService apiService) {
            this.apiService = apiService;
        }

        @GetMapping("/posts")
        public Mono<Void> fetchAndStoreData() {
            return apiService.fetchAndStoreData();
        }
    }

    @Slf4j
    @Service
    public static class ApiService {
        private final WebClient webClient;
        private final Map<String, ResponseDto> responseMap = new ConcurrentHashMap<>();

        public ApiService(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
        }

        public Flux<ResponseDto> getData() {

            List<String> endpoints = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> "/posts/" + 1)
                    .collect(Collectors.toList());

            return Flux.fromIterable(endpoints)
                    .flatMap(endpoint -> webClient.get()
                            .uri(endpoint)
                            .retrieve()
                            .bodyToMono(ResponseDto.class)
                            .subscribeOn(Schedulers.boundedElastic())
                    );
        }

        public Mono<Integer> saveData(Flux<ResponseDto> data) {
            AtomicInteger counter = new AtomicInteger();

            return data
                    .doOnNext(res -> {
                        String key = "id_" + res.getId();
                        if(responseMap.putIfAbsent(key, res) == null)   // 동시성 처리
                            counter.incrementAndGet();
                    })
                    .then(Mono.fromCallable(counter::get));
        }

        public Mono<Void> fetchAndStoreData() {

            Flux<ResponseDto> data = getData();

            return saveData(data)
                    .doOnSuccess(info -> log.info("cnt : {}, resultMap : {}", info, responseMap))
                    .then();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    private static class ResponseDto {
        private Long id;
        private String title;
    }

    public static void main(String[] args) {
        SpringApplication.run(RequestApplication.class, args);
    }
}
