package com.webflux.study.exam01;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class MySub implements Subscriber<Integer> {

    private Subscription subscription;
    private static final long bufferSize = 4;
    private long reqSize;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;

        reqSize = bufferSize;
        log.info("MySub.onSubscribe() reqSize :: {}", reqSize);

        s.request(reqSize);
    }

    @Override
    public void onNext(Integer integer) {
        log.info(" >>>>> >>>>> Send Data :: {}", integer);
        reqSize--;

        if(reqSize == 0) {
            log.info(" >>>>> after");
            reqSize = bufferSize;
            subscription.request(reqSize);
        }
    }

    @Override
    public void onError(Throwable t) {
        log.info("@@@@@ Error during subscription @@@@@");
    }

    @Override
    public void onComplete() {
        log.info("########## End of subscription ##########");
    }
}
