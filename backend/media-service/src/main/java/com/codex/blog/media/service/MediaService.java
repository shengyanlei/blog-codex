package com.codex.blog.media.service;

import com.codex.blog.media.domain.MediaAsset;
import com.codex.blog.media.repository.MediaAssetRepository;
import com.codex.blog.media.web.dto.UploadConfirmRequest;
import com.codex.blog.media.web.dto.UploadPolicyRequest;
import com.codex.blog.media.web.dto.UploadPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaAssetRepository mediaAssetRepository;

    public MediaService(MediaAssetRepository mediaAssetRepository) {
        this.mediaAssetRepository = mediaAssetRepository;
    }

    public UploadPolicyResponse createPolicy(UploadPolicyRequest request) {
        String storageKey = UUID.randomUUID() + "-" + request.getFileName();
        UploadPolicyResponse response = new UploadPolicyResponse();
        response.setStorageKey(storageKey);
        response.setBucket("local-bucket");
        response.setUploadUrl("https://storage.local/upload/" + storageKey);
        response.setExpireAt(Instant.now().plusSeconds(600));
        return response;
    }

    @Transactional
    public MediaAsset confirm(UploadConfirmRequest request, String uploaderId) {
        MediaAsset asset = new MediaAsset();
        asset.setStorageKey(request.getStorageKey());
        asset.setStorageBucket(request.getStorageBucket());
        asset.setOriginalName(request.getOriginalName());
        asset.setMimeType(request.getMimeType());
        asset.setSizeBytes(request.getSizeBytes());
        asset.setWidth(request.getWidth());
        asset.setHeight(request.getHeight());
        asset.setUploaderId(uploaderId);
        return mediaAssetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public Page<MediaAsset> list(int page, int size) {
        return mediaAssetRepository.findAll(PageRequest.of(page, size));
    }
}
