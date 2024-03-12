package com.webflux.study.exam02;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class EmployeeHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Mono<ServerResponse> findEmployeeById(ServerRequest request) {
        Mono<Employee> employee = employeeRepository.findEmployeeById(request.pathVariable("id"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(employee.block()); // TODO .. Mono.block...
    }
}
