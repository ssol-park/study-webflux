package com.webflux.study.exam03;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import java.util.List;

public class PubSubUtil {
    public static Publisher<Integer> iterPub(List<Integer> iter) {
        return sub -> sub.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                iter.forEach(sub::onNext);
                sub.onComplete();
            }

            @Override
            public void cancel() {}
        });
    }

    public static <T> Subscriber<T> loggingSub(Logger logger) {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                logger.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
                logger.info("onNext : {}", i);
            }

            @Override
            public void onError(Throwable t) {
                logger.info("onError: {}", t);
            }

            @Override
            public void onComplete() {
                logger.info("onComplete");
            }
        };
    }
}
