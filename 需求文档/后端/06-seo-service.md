# SEO 与分享服务设计（SEO Service）

## 1. 目标

- 管理全站 SEO 数据，包括 slug、Meta、Open Graph、结构化数据、站点地图、RSS、分享卡片生成。

## 2. 职责

- 监听内容发布事件，生成/更新 sitemap、RSS feed。
- 提供 Meta/OG API 供 BFF/前端读取。
- 生成分享卡片（海报）与 JSON-LD。

## 3. 数据结构

- `seo_configs`: `id`, `tenant_id`, `type(site/post/page)`, `config_json`, `updated_by`, `updated_at`
- `sitemaps`: 记录最新生成时间、URL。
- `share_cards`: `id`, `entity_type`, `entity_id`, `template_id`, `storage_key`, `created_at`

## 4. 流程

1. 接收 `content.post.published` → 更新 `sitemap.xml`、`rss.xml`。
2. 根据 `seo_configs` 模板渲染 meta（title/description/keywords），写入缓存。
3. 分享卡片：触发异步任务，读取文章内容/封面 → 使用 HTML 模板 + Chromium 截图 → 上传到 Media。

## 5. API

| 功能 | Method & Path | Request | Response | 说明 |
| ---- | ------------- | ------- | -------- | ---- |
| 查询站点 meta | `GET /api/v1/seo/site-meta` | `?locale` | `{title, description, keywords}` | BFF 使用 |
| 查询文章 meta | `GET /api/v1/seo/posts/{slug}` | - | `{title, description, ogImage, jsonLd}` | 自动 fallback 到默认模板 |
| 更新模板 | `PUT /api/v1/seo/templates/{type}` | `{configJson}` | `{updatedAt}` | 管理员 |
| 获取 sitemap | `GET /api/v1/seo/sitemap.xml` | - | XML | 也可直接由 CDN 提供 |
| 获取 RSS | `GET /api/v1/seo/rss.xml` | - | XML | 支持多语言 |

## 6. 生成策略

- Sitemap：按日批量更新；大于 50k URL 自动拆分 index。
- RSS：最新 50 篇文章；字段包含 `guid`, `link`, `pubDate`, `content:encoded`.
- Slug 生成 API（供内容服务调用）：`POST /api/v1/seo/slug` → 返回唯一 slug。
- JSON-LD Schema：根据 content type 选择 `Article`, `BreadcrumbList`，字段映射 `author`, `headline`, `datePublished`.

## 7. 依赖

- Content Service（文章数据）
- Media Service（封面、分享卡图）
- Notification（用于推送 RSS 更新? optional）

## 8. 测试

- [ ] 多语言 sitemap 输出
- [ ] slug 生成冲突策略
- [ ] 模板更新后缓存失效
- [ ] 分享卡片生成失败重试
- [ ] RSS 验证通过 W3C Feed Validator
