package com.webflux.study.exam03;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.webflux.study.exam03.PubSubUtil.iterPub;
import static com.webflux.study.exam03.PubSubUtil.loggingSub;

public class GenericPubSub {
    private static final Logger logger = LoggerFactory.getLogger(GenericPubSub.class);
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).toList());
        Publisher<String> mapPub = mapPub(pub, s -> String.format("[%s]", s));

        mapPub.subscribe(loggingSub(logger));
    }

    // T -> R
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> function) {
        return sub -> {

            pub.subscribe(new GenericSub<T, R>(sub) {
                @Override
                public void onNext(T i) {
                    sub.onNext(function.apply(i));
                }
            });
        };
    }
}
