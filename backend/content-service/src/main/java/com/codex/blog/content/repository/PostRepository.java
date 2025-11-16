package com.codex.blog.content.repository;

import com.codex.blog.content.domain.Post;
import com.codex.blog.content.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);
}
