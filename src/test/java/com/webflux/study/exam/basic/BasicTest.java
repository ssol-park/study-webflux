package com.webflux.study.exam.basic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicTest {

    @Test
    void testBasicReactiveStream() {
        BasicPublisher pub = new BasicPublisher();
        BasicSubscriber sub = new BasicSubscriber();

        pub.subscribe(sub);

        assertThat(sub.getReceivedCnt()).isEqualTo(5);
        assertThat(sub.isCompleted()).isTrue();
    }
}
