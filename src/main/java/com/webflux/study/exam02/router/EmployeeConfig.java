package com.webflux.study.exam02.router;

import com.webflux.study.exam02.EmployeeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EmployeeConfig {
    @Bean
    public RouterFunction<ServerResponse> getEmployeeByIdRoute(EmployeeHandler handler) {
        return RouterFunctions.route()
                .GET("/employees/{id}", request -> handler.findEmployeeById(request))
                .GET("/employees", request -> handler.findAllEmployees(request))
                .build();
    }
}
