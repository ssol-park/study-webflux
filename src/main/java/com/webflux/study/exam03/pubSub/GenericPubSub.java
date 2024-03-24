package com.webflux.study.exam03.pubSub;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.webflux.study.exam03.pubSub.PubSubUtil.*;

public class GenericPubSub {
    private static final Logger logger = LoggerFactory.getLogger(GenericPubSub.class);
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).toList());

        Arrays.asList(2).forEach(examNum -> {
            if(examNum == 1) {
                Publisher<String> mapPub = mapPub(pub, s -> String.format("[%s]", s));
                mapPub.subscribe(logSub(logger));
            }
            if(examNum == 2) {
                reducePub(pub, new StringBuilder(), (a, b) -> a.append(String.format("%d,", b))).subscribe(logSub(logger));
            }
        });
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return sub -> {
            pub.subscribe(new GenericSub<T, R>(sub) {
                R result = init;
                @Override
                public void onNext(T i) {
                    result = bf.apply(result, i);
                }

                @Override
                public void onComplete() {
                    sub.onNext(result);
                }
            });
        };
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
