package com.codex.blog.content.web;

import com.codex.blog.content.domain.Category;
import com.codex.blog.content.service.CategoryService;
import com.codex.blog.content.web.dto.CategoryCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody CategoryCreateRequest request) {
        Category category = categoryService.createCategory(
                request.getName(),
                request.getSlug(),
                request.getDescription(),
                request.getParentId(),
                request.getDisplayOrder());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "分类创建成功");
        response.put("data", category);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.updateCategory(
                id,
                request.getName(),
                request.getSlug(),
                request.getDescription(),
                request.getParentId(),
                request.getDisplayOrder());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "分类更新成功");
        response.put("data", category);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "分类删除成功");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listCategories(
            @RequestParam(required = false, defaultValue = "false") boolean tree) {
        List<Category> categories;

        if (tree) {
            // 返回树形结构（仅返回根分类，前端可以递归获取子分类）
            categories = categoryService.getRootCategories();
        } else {
            // 返回扁平列表
            categories = categoryService.getAllCategories();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", categories);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", category);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Map<String, Object>> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", category);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<Map<String, Object>> getChildCategories(@PathVariable Long id) {
        List<Category> children = categoryService.getChildCategories(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", children);

        return ResponseEntity.ok(response);
    }
}
