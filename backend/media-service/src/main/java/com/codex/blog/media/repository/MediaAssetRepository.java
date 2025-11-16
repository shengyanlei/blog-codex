package com.codex.blog.media.repository;

import com.codex.blog.media.domain.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
}
