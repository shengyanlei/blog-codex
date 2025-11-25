package com.codex.blog.content.repository;

import com.codex.blog.content.domain.PostVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostVersionRepository extends JpaRepository<PostVersion, Long> {

    List<PostVersion> findByPostIdOrderByVersionNumberDesc(Long postId);

    @Query("SELECT pv FROM PostVersion pv WHERE pv.postId = :postId ORDER BY pv.versionNumber DESC")
    List<PostVersion> findVersionsByPostId(@Param("postId") Long postId);

    @Query("SELECT pv FROM PostVersion pv WHERE pv.postId = :postId ORDER BY pv.versionNumber DESC LIMIT 1")
    Optional<PostVersion> findLatestVersionByPostId(@Param("postId") Long postId);

    @Query("SELECT MAX(pv.versionNumber) FROM PostVersion pv WHERE pv.postId = :postId")
    Optional<Integer> findMaxVersionNumberByPostId(@Param("postId") Long postId);
}
