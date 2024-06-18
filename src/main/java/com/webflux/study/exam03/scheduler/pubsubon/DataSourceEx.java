package com.webflux.study.exam03.scheduler.pubsubon;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Typically used for slow publisher e.g., blocking IO, fast consumer(s) scenarios.
 * 퍼블리셔가 느리고 (ex 블로킹 I0), 작업을 처리하는 sub 은 빠른 경우 사용 --> pub 을 별도의 스레드에서 실행
 * */
@Slf4j
public class DataSourceEx {
    public static void main(String[] args) {

        ThreadFactory pubThreadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "pubOn-" + counter.incrementAndGet());
            }
        };

        ExecutorService pubExecutorService = Executors.newFixedThreadPool(10, pubThreadFactory);
        Scheduler pubScheduler = Schedulers.fromExecutor(pubExecutorService);

        ThreadFactory subThreadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "subOn-" + counter.incrementAndGet());
            }
        };

        ExecutorService subExecutorService = Executors.newFixedThreadPool(10, subThreadFactory);
        Scheduler subScheduler = Schedulers.fromExecutor(subExecutorService);

        List<String> data = Stream.iterate(1, n -> n + 1)
                                    .limit(15)
                                    .map(n -> "Item" + n)
                                    .collect(Collectors.toList());

        Flux<String> dataSource = Flux.defer(() -> {
           simulateDatabaseFetch();
           return Flux.fromIterable(data);
        });

        dataSource
                .subscribeOn(subScheduler)
                .flatMap(item -> {
                    int num = Integer.parseInt(item.replace("Item", ""));

                    return Flux.just(item)
                            .delayElements(num % 3 == 0 ? Duration.ofMillis(500) : Duration.ZERO)
                            .publishOn(pubScheduler)
                            .doOnNext(i -> log.info("Processing {} on thread: {}", i, Thread.currentThread().getName()))
                            ;
                })
                .doOnNext(item -> log.info("Processed {} on thread: {}", item, Thread.currentThread().getName()))
                .subscribe(item -> log.info("Recevied {} on thread: {}", item, Thread.currentThread().getName()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("exit.");
        subExecutorService.shutdown();
        pubExecutorService.shutdown();
    }

    private static void simulateDatabaseFetch() {
        try {
            log.info("Fetching data from database on thread : {}", Thread.currentThread().getName());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
