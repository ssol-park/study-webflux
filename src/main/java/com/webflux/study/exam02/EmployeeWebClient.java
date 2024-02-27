package com.webflux.study.exam02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class EmployeeWebClient {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeWebClient.class);

    WebClient client = WebClient.create("http://localhost:8080");

    public void consume() {

        Mono<Employee> employeeMono = client.get()
                .uri("/employees/{id}", "1")
                .retrieve()
                .bodyToMono(Employee.class);

        employeeMono.subscribe(employee -> logger.info("Mono employee: {}", employee));
    }
}
