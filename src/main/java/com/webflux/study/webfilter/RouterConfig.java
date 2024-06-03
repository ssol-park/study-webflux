package com.webflux.study.webfilter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(PostHandler postHandler) {
        return route(GET("/posts"), postHandler::findAll)
                .filter(((request, next) -> {
                    // 요청
                    log.info("request uri :: {}", request.uri());

                    Mono<ServerResponse> response = next.handle(request);

                    // 응답
                    return response.doOnNext(res -> log.info("response code :: {}", res.statusCode()));
                }));
    }
}
