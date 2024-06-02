package com.webflux.study.exam03.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class CompletableFutureEx {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);


    /*
    * runnable, consumer, function, bifunction
    * supplyAsync : 매개변수 x, 결과 값 o
    * thenApply : 매개변수 o, 결과 값 o
    * thenAccept : 매개변수 o, 결과 값 x
    * thenCompose : 일반 값이 아닌 completableFuture 가 반환 타입일때...
    * */
    public static void main(String[] args) throws InterruptedException {

        List<Integer> numbers = Arrays.asList(1,2,3,4,5);

        CompletableFuture<String> strCf = CompletableFuture.supplyAsync(() -> {
//            if(true) {
//                throw new RuntimeException("error");
//            }
            return numbers.stream().reduce(0, Integer::sum);
        })
        .whenComplete((res, err) -> {
            if(!ObjectUtils.isEmpty(err)) log.error("{}", err);

            log.info("whenComplete: {}", res);
        }).
        thenApply(res -> {
            return String.valueOf(res);
        });

        log.info("## Result : {}", strCf); // Not completed or Completed normally

        log.info("strCf.join() : {}", strCf.join()); // blocking

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

    private static Runnable addNumber(CompletableFuture cf, int num) {

        return () -> {
            long result = num * num;

            log.info("addNumber :: {}", result);

            cf.complete(result);
        };
    }

    public static void exam02() throws InterruptedException {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
//                    if(true) throw new RuntimeException();
                    return 1;
                })
                .thenCompose(s -> {
                    log.info("thenCompose :: {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApplyAsync(s2 -> {
                    log.info("thenApply :: {}", s2);
                    return s2 + 1;
                }, executorService)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s3 -> log.info("thenAccept :: {}", s3), executorService)
        ;

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

    public static void exam01() throws InterruptedException {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
//                    if(true) throw new RuntimeException();
                    return 1;
                })
                .thenCompose(s -> {
                    log.info("thenCompose s :: {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApply(s2 -> {
                    log.info("thenApply s2 :: {}", s2);
                    return s2 + 1;
                })
                .exceptionally(e -> -10)
                .thenAccept(s3 -> log.info("thenAccept :: {}", s3))
                ;

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
