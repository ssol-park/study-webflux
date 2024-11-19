package com.webflux.study.exam.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class BasicSubscription implements Subscription {

    private final Subscriber<? super Integer> subscriber;
    private final AtomicInteger data = new AtomicInteger(0);
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);

    @Override
    public void request(long n) {
        for (int i = 0; i < n; i++) {
            if (isCanceled.get()) return;

            int currentData = data.getAndIncrement();

            subscriber.onNext(currentData);

            if (currentData == 4) {
                subscriber.onComplete();
                return;
            }
        }
    }

    @Override
    public void cancel() {
        isCanceled.set(true);
        log.info("[BasicSubscription] 구독 취소");
    }
}
