package com.codex.blog.analytics.repository;

import com.codex.blog.analytics.domain.PostMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostMetricRepository extends JpaRepository<PostMetric, Long> {

    List<PostMetric> findByPostIdAndDateBetweenOrderByDateAsc(Long postId, LocalDate from, LocalDate to);
}
