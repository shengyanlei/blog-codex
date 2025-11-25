package com.codex.blog.content.repository;

import com.codex.blog.content.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategory.PostCategoryId> {

    List<PostCategory> findByPostId(Long postId);

    List<PostCategory> findByCategoryId(Long categoryId);

    @Query("SELECT pc.categoryId FROM PostCategory pc WHERE pc.postId = :postId")
    List<Long> findCategoryIdsByPostId(@Param("postId") Long postId);

    void deleteByPostId(Long postId);

    void deleteByPostIdAndCategoryId(Long postId, Long categoryId);
}
