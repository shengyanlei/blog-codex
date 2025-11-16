package com.codex.blog.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "interaction-service", url = "${services.interaction:http://localhost:8083}")
public interface InteractionClient {

    @GetMapping("/api/v1/posts/{postId}/comments")
    Object listComments(@PathVariable("postId") Long postId,
                        @RequestParam("page") int page,
                        @RequestParam("size") int size);
}
