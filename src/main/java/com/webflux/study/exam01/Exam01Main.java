package com.webflux.study.exam01;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Exam01Main {
    public static void main(String[] args) {
        MyPub pub = new MyPub();
        MySub sub = new MySub();

        pub.subscribe(sub);
    }

    /*> Task :Exam01Main.main()
            22:35:38.162 [main] INFO com.webflux.study.exam01.MyPub -- ========== 1. Start Subscription
            22:35:38.165 [main] INFO com.webflux.study.exam01.MyPub -- ========== 2. Create Subscription Info
            22:35:38.165 [main] INFO com.webflux.study.exam01.MySub -- MySub.onSubscribe() reqSize :: 4
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 1
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 2
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 3
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 4
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> after
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 5
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 6
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 7
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 8
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> after
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 9
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub --  >>>>> >>>>> Send Data :: 10
            22:35:38.167 [main] INFO com.webflux.study.exam01.MySub -- ########## End of subscription ##########
            */
}
