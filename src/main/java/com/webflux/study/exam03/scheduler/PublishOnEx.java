package com.webflux.study.exam03.scheduler;

import com.webflux.study.exam03.pubsub.PubSubUtil;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Typically used for fast publisher, slow consumer(s) scenarios.
 * 퍼블리셔는 빠르지만 작업을 실행, 전달 하는 sub 이 느린 경우 사용 --> subscriber 를 별개의 쓰레드에서 동작하도록 함
 * */
public class PublishOnEx {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(PublishOnEx.class);

        List<Integer> list = Arrays.asList(1,2,3,4,5);

        Publisher<Integer> iterPub = PubSubUtil.iterPub(list);

        // publishOn Publisher
        Publisher<Integer> publishOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor();

            iterPub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription s) {
                    sub.onSubscribe(s);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable t) {
                    es.execute(() -> sub.onError(t));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                }
            });
        };

        Subscriber<Integer> logSub = PubSubUtil.logSub();

        publishOnPub.subscribe(logSub);

        logger.info("exit");
    }
}
