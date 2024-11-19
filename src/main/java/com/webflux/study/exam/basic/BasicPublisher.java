package com.webflux.study.exam.basic;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class BasicPublisher implements Publisher<Integer> {
    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new BasicSubscription(subscriber));
    }
}
