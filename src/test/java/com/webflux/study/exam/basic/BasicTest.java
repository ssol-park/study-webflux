package com.webflux.study.exam.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicTest {

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

}
