package com.webflux.study.exam01;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Arrays;

@Slf4j
public class MyPub implements Publisher<Integer> {

    private Iterable<Integer> its = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

    @Override
    public void subscribe(Subscriber sub) {
        log.info("========== 1. Start Subscription");

        MySubscription mySubscription = new MySubscription(sub, its);
        log.info("========== 2. Create Subscription Info");

        sub.onSubscribe(mySubscription);
    }
}
