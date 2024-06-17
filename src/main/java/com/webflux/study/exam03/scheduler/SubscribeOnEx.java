package com.webflux.study.exam03.scheduler;

import com.webflux.study.exam03.pubsub.PubSubUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Typically used for slow publisher e.g., blocking IO, fast consumer(s) scenarios.
 * 퍼블리셔가 느리고 (ex 블로킹 I0), 작업을 처리하는 sub 은 빠른 경우 사용 --> pub 을 별도의 스레드에서 실행
 * */
@Slf4j
public class SubscribeOnEx {
    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "cst-subOn-" + counter.incrementAndGet());
            }
        };

        Scheduler scheduler = Schedulers.fromExecutor(Executors.newSingleThreadExecutor(threadFactory));

        List<String> strings = List.of("A", "B", "C");

        Flux.fromIterable(strings)
                .subscribeOn(scheduler)
                .map(SubscribeOnEx::getLowerCase)
                .subscribe(
                        data -> log.info("data :: {}", data),
                        error -> log.error("error :: {}", error),
                        () -> log.info("complete.")
                );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("exit.");
    }

    private static String getLowerCase(String s) {
        return s.toLowerCase();
    }

    /*List<Integer> list = Arrays.asList(1,2,3,4,5);

    Publisher<Integer> iterPub = PubSubUtil.iterPub(list);

    // subscribeOn Publisher
    Publisher<Integer> subscribeOnPub = sub -> {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> iterPub.subscribe(sub));
    };

    Subscriber<Integer> logSub = PubSubUtil.logSub();

        subscribeOnPub.subscribe(logSub);

        logger.info("exit");*/
}
