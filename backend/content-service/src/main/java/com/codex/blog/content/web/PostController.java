package com.codex.blog.content.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.content.service.PostDto;
import com.codex.blog.content.service.PostService;
import com.codex.blog.content.web.dto.PostCreateRequest;
import com.codex.blog.content.web.dto.PostPublishRequest;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDto>> create(@Valid @RequestBody PostCreateRequest request) {
        PostDto dto = new PostDto();
        dto.setTitle(request.getTitle());
        dto.setContentMd(request.getContentMd());
        dto.setExcerpt(request.getExcerpt());
        dto.setTags(request.getTags());
        dto.setLanguage(request.getLanguage());
        PostDto saved = postService.create(dto, "author-demo");
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.list(page, size)));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<PostDto>> publish(
            @PathVariable Long id,
            @RequestBody(required = false) PostPublishRequest request) {
        PostDto dto = postService.publish(id, request == null ? null : request.getPublishAt());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(postService.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<PostDto>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(postService.getBySlug(slug)));
    }
}
