package com.codex.blog.content.repository;

import com.codex.blog.content.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByParentId(Long parentId);

    List<Category> findByParentIdIsNullOrderByDisplayOrderAsc();

    boolean existsBySlug(String slug);
}
