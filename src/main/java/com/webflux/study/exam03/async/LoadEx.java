package com.webflux.study.exam03.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadEx {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        // Synchronous client to perform HTTP requests 동기방식
        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest";

        // 지정한 수 만큼의 쓰레드가 대기상태가 되면 실행시킴 --> 테스트할 때 여러개의 쓰레드를 모았다가 한 번에 실행시키기 위해 사용함.
        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet(1);

                barrier.await();

                log.info("Thread {}", idx);

                StopWatch st = new StopWatch();
                st.start();

                rt.getForObject(url, String.class);

                st.stop();
                log.info("Elapsed: {} {}", idx, st.getTotalTimeSeconds());

                return null;
            });
        }

        barrier.await();

        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
