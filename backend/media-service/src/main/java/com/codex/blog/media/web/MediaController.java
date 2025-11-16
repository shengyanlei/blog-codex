package com.codex.blog.media.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.media.domain.MediaAsset;
import com.codex.blog.media.service.MediaService;
import com.codex.blog.media.web.dto.UploadConfirmRequest;
import com.codex.blog.media.web.dto.UploadPolicyRequest;
import com.codex.blog.media.web.dto.UploadPolicyResponse;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload-policy")
    public ResponseEntity<ApiResponse<UploadPolicyResponse>> policy(@Valid @RequestBody UploadPolicyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.createPolicy(request)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<MediaAsset>> confirm(@Valid @RequestBody UploadConfirmRequest request) {
        MediaAsset asset = mediaService.confirm(request, "uploader-demo");
        return ResponseEntity.ok(ApiResponse.success(asset));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MediaAsset>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.list(page, size)));
    }
}

