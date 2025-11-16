package com.codex.blog.analytics.service;

import com.codex.blog.analytics.domain.PostMetric;
import com.codex.blog.analytics.repository.PostMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final PostMetricRepository postMetricRepository;

    public AnalyticsService(PostMetricRepository postMetricRepository) {
        this.postMetricRepository = postMetricRepository;
    }

    @Transactional
    public void record(Long postId) {
        LocalDate today = LocalDate.now();
        List<PostMetric> metrics = postMetricRepository.findByPostIdAndDateBetweenOrderByDateAsc(postId, today, today);
        PostMetric metric = metrics.isEmpty() ? new PostMetric() : metrics.get(0);
        metric.setPostId(postId);
        metric.setDate(today);
        metric.setPageViews(metric.getPageViews() + 1);
        postMetricRepository.save(metric);
    }

    @Transactional(readOnly = true)
    public List<PostMetricsDto> metrics(Long postId, LocalDate from, LocalDate to) {
        List<PostMetric> metrics = postMetricRepository.findByPostIdAndDateBetweenOrderByDateAsc(postId, from, to);
        List<PostMetricsDto> dtos = new ArrayList<>();
        for (PostMetric metric : metrics) {
            PostMetricsDto dto = new PostMetricsDto();
            dto.setDate(metric.getDate());
            dto.setPageViews(metric.getPageViews());
            dto.setUniqueVisitors(metric.getUniqueVisitors());
            dto.setLikes(metric.getLikes());
            dto.setComments(metric.getComments());
            dtos.add(dto);
        }
        return dtos;
    }
}
