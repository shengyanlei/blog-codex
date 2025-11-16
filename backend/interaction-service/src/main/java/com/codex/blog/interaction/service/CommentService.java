package com.codex.blog.interaction.service;

import com.codex.blog.common.exception.BusinessException;
import com.codex.blog.interaction.domain.Comment;
import com.codex.blog.interaction.domain.CommentAction;
import com.codex.blog.interaction.repository.CommentActionRepository;
import com.codex.blog.interaction.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentActionRepository commentActionRepository;

    public CommentService(CommentRepository commentRepository,
                          CommentActionRepository commentActionRepository) {
        this.commentRepository = commentRepository;
        this.commentActionRepository = commentActionRepository;
    }

    @Transactional
    public CommentDto create(CommentDto dto) {
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setAuthorId(dto.getAuthorId());
        comment.setContent(dto.getContent());
        comment.setStatus("APPROVED");
        comment.setPath(generatePath(dto.getParentId()));
        comment.setDepth(dto.getParentId() == null ? 0 : 1);
        comment.setCreatedAt(Instant.now());
        return toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> list(Long postId, int page, int size) {
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId, PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional
    public CommentDto like(Long commentId, String userId, boolean like) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(InteractionErrors.COMMENT_NOT_FOUND));
        String actionType = "LIKE";
        CommentAction existing = commentActionRepository
                .findByCommentIdAndUserIdAndActionType(commentId, userId, actionType)
                .orElse(null);
        if (like && existing == null) {
            CommentAction action = new CommentAction();
            action.setCommentId(commentId);
            action.setUserId(userId);
            action.setActionType(actionType);
            commentActionRepository.save(action);
            comment.setLikeCount(comment.getLikeCount() + 1);
        } else if (!like && existing != null) {
            commentActionRepository.delete(existing);
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        }
        return toDto(comment);
    }

    private String generatePath(Long parentId) {
        if (parentId == null) {
            return String.format("%04d", commentRepository.count() + 1);
        }
        return parentId + "." + System.currentTimeMillis();
    }

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setAuthorId(comment.getAuthorId());
        dto.setContent(comment.getContent());
        dto.setStatus(comment.getStatus());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
