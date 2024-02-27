package com.webflux.study.exam02;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class EmployeeConfig {

    @Bean
    EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }

    @Bean
    RouterFunction<ServerResponse> getEmployeeByIdRoute() {
        return route(GET("/employees/{id}"), req -> ok().body(employeeRepository().findEmployeeById(req.pathVariable("id")), Employee.class));
    }
}
