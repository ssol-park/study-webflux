package com.webflux.study.method;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Thread streamThread = new Thread(MapEx::streamFlatMap);
        streamThread.setName("thread-streamFlatMap");

        Thread webFluxThread = new Thread(MapEx::webFluxFlatMap);
        webFluxThread.setName("thread-webFluxFlatMap");

        streamThread.start();
        webFluxThread.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("exit.");
    }

    public static void webFluxFlatMap() {
        log.info("============= START =============");

        Flux<String> sentences = Flux.just("AB CD", "EF GH");
        CountDownLatch latch = new CountDownLatch(1);

        long startTime = System.nanoTime();

        Flux<String> words = sentences.flatMap(sentence ->
                    Flux.fromArray(sentence.split(" "))
                            .delayElements(Duration.ofMillis(500))
                            .subscribeOn(Schedulers.parallel())
                );

        words.doOnComplete(() -> {
            long duration = (System.nanoTime() - startTime);
            log.info("webFluxFlatMap Execution Nano Time {}", duration);
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

        log.info("============= End =============");
    }

    public static void streamFlatMap() {
        log.info("============= START =============");

        List<String> sentences = Arrays.asList("AB CD", "EF GH");

        long startTime = System.nanoTime();

        sentences.stream()
                .flatMap(sentence -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Stream<String> splitSentences = Arrays.stream(sentence.split(" "));
                    log.info("??? {}", splitSentences);

                    return splitSentences;
                }).collect(Collectors.toList());

        long duration = (System.nanoTime() - startTime);

        log.info("streamFlatMap Execution Nano Time {}", duration);
        log.info("============= End =============");
    }
}
