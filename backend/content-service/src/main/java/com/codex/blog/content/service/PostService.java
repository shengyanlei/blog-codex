package com.codex.blog.content.service;

import com.codex.blog.common.exception.BusinessException;
import com.codex.blog.content.domain.Post;
import com.codex.blog.content.domain.PostStatus;
import com.codex.blog.content.domain.Tag;
import com.codex.blog.content.repository.PostRepository;
import com.codex.blog.content.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public PostDto create(PostDto dto, String authorId) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setTitle(dto.getTitle());
        post.setSlug(generateSlug(dto.getTitle()));
        post.setContentMd(dto.getContentMd());
        post.setExcerpt(dto.getExcerpt());
        post.setLanguage(dto.getLanguage() == null ? "zh-CN" : dto.getLanguage());
        post.setTags(resolveTags(dto.getTags()));
        Post saved = postRepository.save(post);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> list(int page, int size) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(page, size));
        return posts.map(this::toDto);
    }

    @Transactional
    public PostDto publish(Long id, Instant publishAt) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PostErrors.POST_NOT_FOUND));
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishAt(publishAt == null ? Instant.now() : publishAt);
        post.setUpdatedAt(Instant.now());
        return toDto(post);
    }

    @Transactional(readOnly = true)
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PostErrors.POST_NOT_FOUND));
        return toDto(post);
    }

    @Transactional(readOnly = true)
    public PostDto getBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(PostErrors.POST_NOT_FOUND));
        return toDto(post);
    }

    private Set<Tag> resolveTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }
        Set<Tag> result = new HashSet<>();
        for (String tagName : tags) {
            if (!StringUtils.hasText(tagName)) {
                continue;
            }
            Tag tag = tagRepository.findByName(tagName.trim())
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(tagName.trim());
                        return tagRepository.save(t);
                    });
            result.add(tag);
        }
        return result;
    }

    private String generateSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        String slug = normalized.isEmpty() ? "post-" + System.currentTimeMillis() : normalized;
        if (postRepository.findBySlug(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        return slug;
    }

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setContentMd(post.getContentMd());
        dto.setExcerpt(post.getExcerpt());
        dto.setStatus(post.getStatus().name());
        dto.setPublishAt(post.getPublishAt());
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tagNames.add(tag.getName());
        }
        dto.setTags(tagNames);
        return dto;
    }
}
