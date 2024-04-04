package com.webflux.study.exam03.scheduler;

import com.webflux.study.exam03.pubSub.PubSubUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxSchedulerEx {
    public static void main(String[] args) throws InterruptedException {

        int examNum = 3;

        if(examNum == 1) {
            Flux.range(0, 5)
                    .publishOn(Schedulers.newSingle("pubOn-"))
                    .log()
//                .subscribeOn(Schedulers.newSingle("subOn-"))
                    .subscribe(System.out::println)
            ;
            log.info("exam-1 exit");
        }

        if(examNum == 2) {
            // User thread, Daemon thread
            Flux.interval(Duration.ofMillis(500))
                    .subscribe(s -> log.info("onNext:{}", s));

            log.info("exam-2 exit.");
            TimeUnit.SECONDS.sleep(2); // interval() --> daemon thread 이므로, sleep 을 걸어주지 않으면 main thread 종료 시 즉시 종료됨
        }

        if(examNum == 3) {
            /*Flux.interval(Duration.ofMillis(200))
                    .take(5)
                    .subscribe(s -> log.info("onNext:{}", s));*/

            Publisher<Integer> pub = sub -> {
                sub.onSubscribe(new Subscription() {
                    int no = 0;
                    boolean isCancel = false;

                    @Override
                    public void request(long n) {
                        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

                        exec.scheduleAtFixedRate(() -> {

                            if(isCancel) {
                                exec.shutdown();
                                return;
                            }

                            sub.onNext(no++);
                        }, 0, 300, TimeUnit.MILLISECONDS);

                    }

                    @Override
                    public void cancel() {
                        isCancel = true;
                    }
                });
            };

            Publisher<Integer> takePub = sub -> {
                pub.subscribe(new Subscriber<>() {
                    int count = 0;
                    Subscription subSc;

                    @Override
                    public void onSubscribe(Subscription s) {
                        subSc = s;
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(integer);

                        if(++count >= 5) {
                            subSc.cancel();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            };

            takePub.subscribe(PubSubUtil.logSub());

            log.info("exam-3 exit.");
            TimeUnit.SECONDS.sleep(2);
        }

    }
}
