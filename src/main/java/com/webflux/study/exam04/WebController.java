package com.webflux.study.exam04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/web-flux")
public class WebController {
    private static final String URL = "http://localhost:8080/web-flux/service?idx={idx}";

    private final WebService webService;

    public WebController(WebService webService) {
        this.webService = webService;
    }

    private WebClient client = WebClient.create();

    @GetMapping
    public Mono<String> getServiceByIdx(@RequestParam(name = "idx") int idx) {
        log.info("############ getServiceByIdx : {}", idx);

        return client.get()
                .uri(URL, idx)
                .exchangeToMono(res -> res.bodyToMono(String.class));
    }

    @GetMapping("/service")
    public Mono<String> service(@RequestParam(name = "idx") int idx) throws InterruptedException{
        log.info("service :: {}", idx);
        return Mono.just(String.valueOf(webService.getLoad(idx)));
    }

    @GetMapping("/load")
    public Mono<String> load(@RequestParam(name = "idx") int idx) {
        return Mono.just(String.valueOf(idx));
    }
}
