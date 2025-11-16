package com.codex.blog.gateway.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.gateway.client.ContentClient;
import com.codex.blog.gateway.client.InteractionClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bff/v1")
public class HomeController {

    private final ContentClient contentClient;
    private final InteractionClient interactionClient;

    public HomeController(ContentClient contentClient, InteractionClient interactionClient) {
        this.contentClient = contentClient;
        this.interactionClient = interactionClient;
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeResponse>> home(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Object posts = contentClient.listPosts(page, size);
        HomeResponse response = new HomeResponse();
        response.setFeed(posts);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Object>> comments(@PathVariable Long postId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(interactionClient.listComments(postId, page, size)));
    }

    public static class HomeResponse {
        private Object feed;

        public Object getFeed() {
            return feed;
        }

        public void setFeed(Object feed) {
            this.feed = feed;
        }
    }
}
