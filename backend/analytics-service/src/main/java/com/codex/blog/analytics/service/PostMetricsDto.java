package com.codex.blog.analytics.service;

import java.time.LocalDate;

public class PostMetricsDto {

    private LocalDate date;
    private long pageViews;
    private long uniqueVisitors;
    private long likes;
    private long comments;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getPageViews() {
        return pageViews;
    }

    public void setPageViews(long pageViews) {
        this.pageViews = pageViews;
    }

    public long getUniqueVisitors() {
        return uniqueVisitors;
    }

    public void setUniqueVisitors(long uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }
}
