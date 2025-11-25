package com.codex.blog.content.service;

import com.codex.blog.content.domain.Category;
import com.codex.blog.content.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(String name, String slug, String description, Long parentId, Integer displayOrder) {
        if (categoryRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("分类 slug 已存在: " + slug);
        }

        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new IllegalArgumentException("父分类不存在: " + parentId);
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(description);
        category.setParentId(parentId);
        category.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String slug, String description, Long parentId,
            Integer displayOrder) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在: " + id));

        if (slug != null && !slug.equals(category.getSlug())) {
            if (categoryRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("分类 slug 已存在: " + slug);
            }
            category.setSlug(slug);
        }

        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }
        if (parentId != null) {
            if (parentId.equals(id)) {
                throw new IllegalArgumentException("分类不能设置自己为父分类");
            }
            if (!categoryRepository.existsById(parentId)) {
                throw new IllegalArgumentException("父分类不存在: " + parentId);
            }
            category.setParentId(parentId);
        }
        if (displayOrder != null) {
            category.setDisplayOrder(displayOrder);
        }

        category.setUpdatedAt(Instant.now());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("分类不存在: " + id);
        }

        // 检查是否有子分类
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("该分类下有子分类，无法删除");
        }

        categoryRepository.deleteById(id);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在: " + id));
    }

    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在: " + slug));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNullOrderByDisplayOrderAsc();
    }

    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }
}
