package com.webflux.study.exam03.scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxSchedulerEx {
    public static void main(String[] args) throws InterruptedException {

        int examNum = 2;

        if(examNum == 1) {
            Flux.range(0, 5)
                    .publishOn(Schedulers.newSingle("pubOn-"))
                    .log()
//                .subscribeOn(Schedulers.newSingle("subOn-"))
                    .subscribe(System.out::println)
            ;
            log.info("exam-1 exit");
        }

        if(examNum == 2) {
            // User thread, Daemon thread
            Flux.interval(Duration.ofMillis(500))
                    .subscribe(s -> log.info("onNext:{}", s));

            log.info("exam-2 exit.");
            TimeUnit.SECONDS.sleep(2); // interval() --> daemon thread 이므로, sleep 을 걸어주지 않으면 main thread 종료 시 즉시 종료됨
        }


    }
}
