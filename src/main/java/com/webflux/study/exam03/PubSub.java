package com.webflux.study.exam03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class PubSub {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).toList());

        Subscriber<Integer> subscriber = loggingSub();

        pub.subscribe(subscriber);
    }

    private static Subscriber<Integer> loggingSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
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

    private static Publisher<Integer> iterPub(List<Integer> iter) {
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
}
