package com.codex.blog.analytics.web;

import com.codex.blog.analytics.service.AnalyticsService;
import com.codex.blog.analytics.service.PostMetricsDto;
import com.codex.blog.common.dto.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/posts/{postId}/record")
    public ResponseEntity<ApiResponse<Void>> record(@PathVariable Long postId) {
        analyticsService.record(postId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<PostMetricsDto>>> metrics(
            @PathVariable Long postId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.metrics(postId, from, to)));
    }
}
