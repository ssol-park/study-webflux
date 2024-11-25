package com.webflux.study.exam.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class BasicTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);

    @Test
    @DisplayName("기본적인 Reactive Stream 동작 테스트")
    void testReactiveStream() {
        BasicPublisher pub = new BasicPublisher();
        BasicSubscriber sub = new BasicSubscriber();

        pub.subscribe(sub);

        assertThat(sub.getReceivedCnt()).isEqualTo(5);
        assertThat(sub.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("구독 취소 테스트")
    void testSubscriptionCancel() {
        BasicPublisher pub = new BasicPublisher();
        BasicSubscriber sub = new BasicSubscriber();

        pub.subscribe(sub);
        int beforeCnt = sub.getReceivedCnt();

        // 구독 취소
        sub.cancelSubscription();

        // 구독이 취소 후 2건의 데이터를 추가 요청
        sub.getSubscription().request(2);
        int afterCnt = sub.getReceivedCnt();

        assertThat(beforeCnt).isEqualTo(afterCnt);
    }

    @Test
    @DisplayName("예외 처리 테스트")
    void testErrorHandling() {
        BasicPublisher pub = new BasicPublisher();
        BasicSubscriber sub = new BasicSubscriber() {
            @Override
            public void onNext(Integer item) {
                super.onNext(item);

                if (item == 3) {
                    throw new RuntimeException("#### 에러 발생: " + item);
                }
            }
        };

        pub.subscribe(sub);

        assertThat(sub.isCompleted()).isFalse();
        assertThat(sub.getErrorCnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("재시도 테스트")
    void testRetry() {
        BasicPublisher pub = new BasicPublisher();
        BasicSubscriber sub = new BasicSubscriber() {
            private int retryCnt = 0;
            private final int maxRetryCnt = 3;

            @Override
            public void onNext(Integer item) {
                super.onNext(item);

                if (item == 3 && retryCnt < maxRetryCnt) {
                    retryCnt++;
                    logger.info("[BasicSubscriber] 재시도 횟수: {}", retryCnt);
                    throw new RuntimeException("#### 에러 발생: " + item);
                }
            }

            @Override
            public void onError(Throwable t) {
                super.onError(t);

                if (retryCnt < maxRetryCnt) {
                    logger.info("[BasicSubscriber] 재시도 요청 시작");
                    getSubscription().request(1);
                }
            }
        };

        pub.subscribe(sub);

        // 최초 오류 + 3회 재시도
        assertThat(sub.getErrorCnt()).isEqualTo(4);
    }
}
