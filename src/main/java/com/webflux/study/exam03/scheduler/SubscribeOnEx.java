package com.webflux.study.exam03.scheduler;

import com.webflux.study.exam03.pubsub.PubSubUtil;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Typically used for slow publisher e.g., blocking IO, fast consumer(s) scenarios.
 * 퍼블리셔가 느리고 (ex 블로킹 I0), 작업을 처리하는 sub 은 빠른 경우 사용 --> pub 을 별도의 스레드에서 실행
 * */
public class SubscribeOnEx {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(SubscribeOnEx.class);

        List<Integer> list = Arrays.asList(1,2,3,4,5);

        Publisher<Integer> iterPub = PubSubUtil.iterPub(list);

        // subscribeOn Publisher
        Publisher<Integer> subscribeOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> iterPub.subscribe(sub));
        };

        Subscriber<Integer> logSub = PubSubUtil.logSub();

        subscribeOnPub.subscribe(logSub);

        logger.info("exit");
    }
}
