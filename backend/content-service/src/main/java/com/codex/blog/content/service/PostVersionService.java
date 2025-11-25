package com.codex.blog.content.service;

import com.codex.blog.content.domain.PostVersion;
import com.codex.blog.content.repository.PostVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PostVersionService {

    private final PostVersionRepository postVersionRepository;

    public PostVersionService(PostVersionRepository postVersionRepository) {
        this.postVersionRepository = postVersionRepository;
    }

    @Transactional
    public PostVersion saveVersion(Long postId, String contentSnapshot, String contentDiff,
            String versionComment, String createdBy) {
        // 获取当前最大版本号
        Integer maxVersion = postVersionRepository.findMaxVersionNumberByPostId(postId)
                .orElse(0);

        PostVersion version = new PostVersion();
        version.setPostId(postId);
        version.setVersionNumber(maxVersion + 1);
        version.setContentSnapshot(contentSnapshot);
        version.setContentDiff(contentDiff);
        version.setVersionComment(versionComment);
        version.setCreatedBy(createdBy);
        version.setCreatedAt(Instant.now());

        return postVersionRepository.save(version);
    }

    public List<PostVersion> getVersionHistory(Long postId) {
        return postVersionRepository.findByPostIdOrderByVersionNumberDesc(postId);
    }

    public PostVersion getVersion(Long versionId) {
        return postVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + versionId));
    }

    public PostVersion getLatestVersion(Long postId) {
        return postVersionRepository.findLatestVersionByPostId(postId)
                .orElse(null);
    }

    /**
     * 简单的差异计算（实际应用中可以使用更复杂的 diff 算法）
     */
    public String calculateDiff(String oldContent, String newContent) {
        if (oldContent == null || oldContent.isEmpty()) {
            return "初始版本";
        }

        // 这里简化处理，实际应该使用 diff 库如 java-diff-utils
        if (oldContent.equals(newContent)) {
            return "无变化";
        }

        return String.format("内容已更新 (旧长度: %d, 新长度: %d)",
                oldContent.length(), newContent.length());
    }
}
