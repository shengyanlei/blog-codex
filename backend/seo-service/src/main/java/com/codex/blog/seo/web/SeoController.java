package com.codex.blog.seo.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.seo.service.SeoMeta;
import com.codex.blog.seo.service.SeoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seo")
public class SeoController {

    private final SeoService seoService;

    public SeoController(SeoService seoService) {
        this.seoService = seoService;
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<ApiResponse<SeoMeta>> meta(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(seoService.forPost(slug)));
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        return seoService.sitemap();
    }

    @GetMapping(value = "/rss.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String rss() {
        return seoService.rss();
    }
}
