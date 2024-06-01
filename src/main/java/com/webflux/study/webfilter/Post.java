package com.webflux.study.webfilter;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String content;

    @Builder
    public Post(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
