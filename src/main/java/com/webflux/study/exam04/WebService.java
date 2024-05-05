package com.webflux.study.exam04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class WebService {

    private static AtomicInteger counter = new AtomicInteger(0);
    public int getLoad(int idx) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        // Synchronous client to perform HTTP requests 동기방식
        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/web-flux/load";

        // 지정한 수 만큼의 쓰레드가 대기상태가 되면 실행시킴 --> 테스트할 때 여러개의 쓰레드를 모았다가 한 번에 실행시키기 위해 사용함.

        for (int i = 0; i < idx; i++) {
            es.submit(() -> {
                int cnt = counter.addAndGet(1);

                log.info("Thread {}", cnt);

                StopWatch st = new StopWatch();
                st.start();

                rt.getForObject(url, String.class);

                st.stop();
                log.info("Elapsed: {} {}", cnt, st.getTotalTimeSeconds());

                return null;
            });
        }

        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());

        return counter.get();
    }
}
