package com.webflux.study.webfilter;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Component
public class PostRepository {
    private Map<Long, Post> data = new HashMap<>();
    private AtomicLong nextIdGenerator = new AtomicLong(1L);

    public PostRepository() {
        Stream.of("post 1", "post 2").forEach(title -> {
            Long id = this.nextId();
            data.put(id, Post.builder().id(id).title(title).content(String.format("content of %s", title)).build());
        });
    }

    private Long nextId() {
        return nextIdGenerator.getAndIncrement();
    }

    public Flux<Post> findAll() {
        return Flux.fromIterable(data.values());
    }
}
