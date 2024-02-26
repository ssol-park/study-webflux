package com.webflux.study.exam01;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;

/*
* 구독 정보(구독자, 어떤 정보를 구독할지)
* */
public class MySubscription implements Subscription {

    private Subscriber<Integer> sub;
    private Iterator<Integer> its;

    public MySubscription(Subscriber<Integer> sub, Iterable<Integer> its) {
        this.sub = sub;
        this.its = its.iterator();
    }

    @Override
    public void request(long n) {
        while (n > 0) {

            if(its.hasNext()) {
                sub.onNext(its.next());
            }else {
                sub.onComplete();
                break;
            }

            n--;
        }
    }

    @Override
    public void cancel() {

    }
}
