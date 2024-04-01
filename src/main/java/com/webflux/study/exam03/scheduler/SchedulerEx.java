package com.webflux.study.exam03.scheduler;

import com.webflux.study.exam03.pubSub.PubSubUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerEx {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(SchedulerEx.class);

        List<Integer> list = Arrays.asList(1,2,3,4,5);

        Publisher<Integer> iterPub = PubSubUtil.iterPub(list);

        Publisher<Integer> subscribeOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {return "subOn-";}
            });
            es.execute(() -> iterPub.subscribe(sub));
        };

        // publishOn Publisher
        Publisher<Integer> publishOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {return "pubOn-";}
            });

            subscribeOnPub.subscribe(new Subscriber<Integer>() {
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

        Subscriber<Integer> logSub = PubSubUtil.logSub(logger);

        publishOnPub.subscribe(logSub);

        logger.info("exit");
    }
}
