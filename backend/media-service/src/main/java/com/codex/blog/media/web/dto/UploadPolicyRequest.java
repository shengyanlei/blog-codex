package com.codex.blog.media.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class UploadPolicyRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String mimeType;

    @Min(1)
    private long size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}

