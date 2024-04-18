package com.webflux.study.exam03.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

@Slf4j
public class PubSubUtil {
    public static Publisher<Integer> iterPub(List<Integer> iter) {
        return sub -> sub.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                System.out.println("request()");
                iter.forEach(sub::onNext);
                sub.onComplete();
            }

            @Override
            public void cancel() {}
        });
    }

    public static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
                log.info("onNext : {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError: {}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }
}
