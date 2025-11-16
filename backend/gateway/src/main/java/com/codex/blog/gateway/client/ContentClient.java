package com.codex.blog.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "content-service", url = "${services.content:http://localhost:8082}")
public interface ContentClient {

    @GetMapping("/api/v1/posts")
    Object listPosts(@RequestParam("page") int page, @RequestParam("size") int size);
}
