package com.webflux.study.exam.basic;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BasicSubscriber implements Subscriber<Integer> {

    private Subscription subscription;
    private final AtomicInteger receivedCnt = new AtomicInteger(0);
    private final AtomicBoolean isCompleted = new AtomicBoolean(false);

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;

        log.info("[BasicSubscriber] 구독 시작");
        subscription.request(3);
    }

    @Override
    public void onNext(Integer item) {
        log.info("[BasicSubscriber] onNext, item :: {}", item);
        receivedCnt.incrementAndGet();

        if (item == 2) {
            log.info("[BasicSubscriber] onNext, 추가 요청 2");
            subscription.request(2);
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("[BasicSubscriber] onError :: {}", t.getMessage(), t);
    }

    @Override
    public void onComplete() {
        log.info("[BasicSubscriber] onComplete, 모든 데이터 전송 완료");
        isCompleted.set(true);
    }

    public int getReceivedCnt() {
        return receivedCnt.get();
    }

    public boolean isCompleted() {
        return isCompleted.get();
    }
}
