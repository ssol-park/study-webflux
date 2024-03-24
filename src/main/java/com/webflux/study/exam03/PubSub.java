package com.webflux.study.exam03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.webflux.study.exam03.PubSubUtil.iterPub;
import static com.webflux.study.exam03.PubSubUtil.loggingSub;

public class PubSub {
    private static final Logger logger = LoggerFactory.getLogger(PubSub.class);

    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).toList());

        Arrays.asList(1,2,3).forEach(examNum -> {
            if(examNum == 1) {
               logger.info(" ========== START EXAM 01 ==========");
               mapPub(pub, (Function<Integer, Integer>)s -> s * 10).subscribe(loggingSub(logger));
            }
            if(examNum == 2) {
               logger.info(" ========== START EXAM 02 ==========");
               mapPub(mapPub(pub, (Function<Integer, Integer>)s -> s * 10), s -> -s).subscribe(loggingSub(logger));
            }
            if(examNum == 3) {
                logger.info(" ========== START EXAM 03 ==========");
               reducePub(pub, 0, (a, b) -> a + b).subscribe(loggingSub(logger));
            }
        });
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> function) {
        return sub -> {
            pub.subscribe(new DelegateSub(sub) {

                @Override
                public void onNext(Integer i) {
                    sub.onNext(function.apply(i));
                }
            });
        };
    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> bf) {
        return sub -> {
            pub.subscribe(new DelegateSub(sub) {
                int result = init;

                @Override
                public void onNext(Integer i) {
                    result = bf.apply(result, i);
                }

                @Override
                public void onComplete() {
                    sub.onNext(result);
                }
            });
        };
    }
}
