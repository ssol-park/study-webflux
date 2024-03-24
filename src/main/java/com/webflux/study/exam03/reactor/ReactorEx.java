package com.webflux.study.exam03.reactor;

import reactor.core.publisher.Flux;

public class ReactorEx {
    public static void main(String[] args) {
        /*
        * log() : 내부 동작의 흐름을 볼 수 있음
        * */
        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.next(4);
            e.next(5);
            e.complete();
        })
        .map(m -> m * 10)
        .filter(f -> f > 20)
        .reduce(0, (a,b) -> a + b)
        .log()
        .subscribe(System.out::println);
    }
}
