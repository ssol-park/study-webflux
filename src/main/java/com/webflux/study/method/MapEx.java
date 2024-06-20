package com.webflux.study.method;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*
* Stream
*   map(): 스트림의 각 요소에 대해 지정된 함수를 적용하여 새로운 요소를 생성. - 동기적으로 작동
*   flatMap():
*
* WebFlux
*   map(): 리액티브 스트림의 각 요소에 대해 동기적인 변환을 수행 - 비동기로 작동하는 리액티브 스트림 내에서 동기적으로 작동함
*   flatMap():
* */
@Slf4j
public class MapEx {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> streamFuture = CompletableFuture.runAsync(MapEx::streamFlatMap, executorService);
        CompletableFuture<Void> webFluxFuture = CompletableFuture.runAsync(MapEx::webFluxFlatMap, executorService);

        CompletableFuture.allOf(streamFuture, webFluxFuture)
                .thenRun(() -> {
                    log.info("all task completed.");
                    executorService.shutdown();
                });


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("exit.");
    }

    public static void webFluxFlatMap() {
        log.info("start webFluxFlatMap()");

        Flux<String> sentences = Flux.just("A B", "C D", "E F", "G H");
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(1);

        ExecutorService fluxEs = Executors.newFixedThreadPool(2, r -> new Thread(r, "webflux-pool-" + counter.incrementAndGet()));

        Scheduler scheduler = Schedulers.fromExecutorService(fluxEs);

        long startTime = System.nanoTime();

        Flux<String> words = sentences.flatMap(sentence ->
                    Flux.fromArray(sentence.split(" "))
                            .delayElements(Duration.ofMillis(500))
                            .publishOn(scheduler)
                );

        words.doOnComplete(() -> {
            long duration = (System.nanoTime() - startTime) / 1000000;
            log.info("webFluxFlatMap Execution Time {}", duration);
            latch.countDown();
        }).subscribe(
                res -> log.info("{}", res),
                err -> log. error("{}", err)
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void streamFlatMap() {
        log.info("start streamFlatMap()");

        List<String> sentences = Arrays.asList("A B", "C D", "E F", "G H");

        long startTime = System.nanoTime();

        sentences.stream()
                .flatMap(sentence -> {
                    try {
                        log.info("Sleep ....");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return Arrays.stream(sentence.split(" "));
                }).collect(Collectors.toList());

        long duration = (System.nanoTime() - startTime) / 1000000;

        log.info("streamFlatMap Execution Time {}", duration);
    }
}
