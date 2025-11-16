package com.codex.blog.interaction.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentCreateRequest {

    @NotNull
    private Long postId;

    @NotBlank
    private String content;

    private Long parentId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}

