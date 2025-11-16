package com.codex.blog.content.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.List;

public class PostCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String contentMd;

    @Size(max = 200)
    private String excerpt;

    private List<String> tags;

    private String language;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentMd() {
        return contentMd;
    }

    public void setContentMd(String contentMd) {
        this.contentMd = contentMd;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
