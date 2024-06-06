package com.webflux.study.webfilter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PostHandler {
    private final PostRepository postRepository;

    public PostHandler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Mono<ServerResponse> findAll(ServerRequest req) {
        return ServerResponse.ok().body(postRepository.findAll(), Post.class);
    }
}
