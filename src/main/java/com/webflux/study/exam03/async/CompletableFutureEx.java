package com.webflux.study.exam03.async;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureEx {
    /*
    * runnable, consumer, function, bifunction
    * supplyAsync : 매개변수 x, 결과 값 o
    * thenApply : 매개변수 o, 결과 값 o
    * thenAccept : 매개변수 o, 결과 값 x
    * thenCompose : 일반 값이 아닌 completableFuture 가 반환 타입일때...
    * */
    public static void main(String[] args) throws InterruptedException {
        /*CompletableFuture
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
                ;*/

        ExecutorService executorService = Executors.newFixedThreadPool(10);

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

}
