package com.webflux.study.exam03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class PubSub {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).toList());
        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Publisher<Integer> mapPub2 = mapPub(mapPub, s -> -s);
        Publisher<Integer> sumPub = sumPub(pub);

        sumPub.subscribe(loggingSub());
    }

    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
        return  sub -> {
            pub.subscribe(new DelegateSub(sub) {
                int sum = 0;
                @Override
                public void onNext(Integer i) {
                    sum += i;
                }

                @Override
                public void onComplete() {
                    sub.onNext(sum);
                    sub.onComplete();
                }
            });
        };
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> function) {
        return sub -> {

            pub.subscribe(new DelegateSub(sub) {
                @Override
                public void onNext(Integer i) {
                    super.onNext(function.apply(i));
                }
            });
        };
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
