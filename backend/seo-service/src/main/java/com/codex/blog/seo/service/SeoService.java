package com.codex.blog.seo.service;

import org.springframework.stereotype.Service;

@Service
public class SeoService {

    public SeoMeta forPost(String slug) {
        SeoMeta meta = new SeoMeta();
        meta.setTitle("文章 " + slug + " - Codex Blog");
        meta.setDescription("Codex Blog 分享技术与产品心得。");
        meta.setKeywords("blog,tech,codex");
        meta.setOgImage("https://example.com/cover.png");
        return meta;
    }

    public String sitemap() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                  <url>
                    <loc>https://codex.example.com/</loc>
                    <priority>1.0</priority>
                  </url>
                </urlset>
                """;
    }

    public String rss() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <rss version="2.0">
                  <channel>
                    <title>Codex Blog</title>
                    <link>https://codex.example.com/</link>
                    <description>最新文章</description>
                  </channel>
                </rss>
                """;
    }
}
