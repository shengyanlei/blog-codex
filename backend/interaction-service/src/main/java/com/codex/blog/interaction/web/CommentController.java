package com.codex.blog.interaction.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.interaction.service.CommentDto;
import com.codex.blog.interaction.service.CommentService;
import com.codex.blog.interaction.web.dto.CommentCreateRequest;
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
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> create(@PathVariable Long postId,
                                                          @Valid @RequestBody CommentCreateRequest request) {
        CommentDto dto = new CommentDto();
        dto.setPostId(postId);
        dto.setAuthorId("user-demo");
        dto.setContent(request.getContent());
        dto.setStatus("APPROVED");
        CommentDto saved = commentService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CommentDto>>> list(@PathVariable Long postId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(commentService.list(postId, page, size)));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse<CommentDto>> like(@PathVariable Long postId,
                                                        @PathVariable Long commentId,
                                                        @RequestParam(defaultValue = "true") boolean like) {
        CommentDto dto = commentService.like(commentId, "user-demo", like);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}

