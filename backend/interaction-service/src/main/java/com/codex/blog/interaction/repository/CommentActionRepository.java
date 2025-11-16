package com.codex.blog.interaction.repository;

import com.codex.blog.interaction.domain.CommentAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentActionRepository extends JpaRepository<CommentAction, Long> {

    Optional<CommentAction> findByCommentIdAndUserIdAndActionType(Long commentId, String userId, String actionType);
}
