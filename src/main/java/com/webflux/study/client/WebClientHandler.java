package com.webflux.study.client;

import com.webflux.study.exam02.Employee;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class WebClientHandler {

    private final WebClientConfig webClientConfig;

    public WebClientHandler(WebClientConfig webClientConfig) {
        this.webClientConfig = webClientConfig;
    }

    public Mono<ServerResponse> getTest() {
        Mono<Employee> employee = webClientConfig.webClient()
                .get()
                .uri("/employees")
                .retrieve()
                .bodyToMono(Employee.class);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(employee.map(a -> a.getId()));
    }
}
