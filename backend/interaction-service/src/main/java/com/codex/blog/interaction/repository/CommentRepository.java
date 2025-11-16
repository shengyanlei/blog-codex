package com.codex.blog.interaction.repository;

import com.codex.blog.interaction.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);
}
