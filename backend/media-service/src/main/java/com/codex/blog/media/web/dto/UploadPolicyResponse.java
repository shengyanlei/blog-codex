package com.codex.blog.media.web.dto;

import java.time.Instant;

public class UploadPolicyResponse {

    private String uploadUrl;
    private String storageKey;
    private String bucket;
    private Instant expireAt;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }
}
