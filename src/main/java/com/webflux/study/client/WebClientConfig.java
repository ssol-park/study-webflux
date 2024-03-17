package com.webflux.study.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

    @Bean
    public WebClient webClient() {

        // timeout
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)))
                ;

        return WebClient.builder()
                .uriBuilderFactory(factory)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        request -> {
                            log.info("Request: {} {}", request.method(), request.url());
                            request.headers().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                            return Mono.just(request);
                        }
                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(Mono::just))
                .defaultHeader("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

}
