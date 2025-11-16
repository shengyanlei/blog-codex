package com.codex.blog.content.web.dto;

import java.time.Instant;

public class PostPublishRequest {

    private Instant publishAt;

    public Instant getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Instant publishAt) {
        this.publishAt = publishAt;
    }
}
