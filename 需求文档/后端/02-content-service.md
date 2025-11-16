# 内容管理服务设计（Content Service）

## 1. 模块目标

- 管理文章全生命周期：草稿、编辑、版本、发布、定时、标签/分类、多语言。
- 提供结构化 API 给后台、BFF、插件，同时向搜索、SEO、通知服务推送事件。

## 2. 业务范围

- **包含**：文章 CRUD、版本 diff、置顶、摘要、元数据、标签/分类维护、定时发布、权限校验。
- **不包含**：媒体文件（交给 Media Service）、评论互动（交给 Interaction）。

## 3. 数据模型

- `posts`：`id`, `tenant_id`, `title`, `slug`, `status(draft/pending/published/archived)`, `content_md`, `content_html`, `excerpt`, `cover_media_id`, `author_id`, `publish_at`, `language`, `featured`, `seo_meta`, `created_at`, `updated_at`
- `post_versions`：`id`, `post_id`, `version`, `diff_json`, `editor_id`, `created_at`
- `tags`, `categories`, `post_tag`, `post_category`
- `post_translations`：关联多语言版本
- `post_workflows`（可选）：`post_id`, `current_state`, `assignee_id`

索引：`posts(slug, tenant_id)` 唯一；`posts(status, publish_at)`；全文检索交由 Elasticsearch。

## 4. 核心流程

1. **创建草稿**：作者创建 -> `posts.status=draft` -> 自动保存版本。
2. **编辑**：Patch 内容 -> 生成新版本 diff（使用 JsonPatch） -> 同步 `updated_at`。
3. **发布**：手动或定时 -> 校验权限/状态 -> 写 `publish_at` -> 推送事件至 SEO、BFF 缓存失效。
4. **回滚**：选择版本 -> 应用 diff -> 新增一条版本记录。

## 5. API（REST）

| 功能 | Method & Path | Request 重点 | Response | 权限 |
| ---- | ------------- | ------------ | -------- | ---- |
| 创建文章 | `POST /api/v1/posts` | `{title, contentMd, tags[], categories[], language}` | `{id, version}` | 作者 |
| 更新文章 | `PUT /api/v1/posts/{id}` | `{title?, contentMd?, excerpt?, metadata}` | `{version}` | 作者/编辑 |
| 获取详情 | `GET /api/v1/posts/{id}` | Query: `includeDraft=true/false` | `PostDTO` | 仅作者/管理员可见草稿 |
| 发布 | `POST /api/v1/posts/{id}/publish` | `{publishAt?, force?}` | `{status}` | 作者+审核通过 |
| 版本列表 | `GET /api/v1/posts/{id}/versions` | - | `[{version, editor, createdAt}]` | |
| 版本对比 | `GET /api/v1/posts/{id}/versions/{v}` | - | `{diff}` | |
| 标签 CRUD | `/api/v1/tags` | | | 管理员/编辑 |

响应示例：

```
{
  "code": "OK",
  "data": {
    "id": "post_123",
    "title": "...",
    "status": "draft",
    "tags": ["java", "spring"],
    "versions": [
      {"version":1,"createdAt":"2025-11-16T00:00:00Z"}
    ]
  }
}
```

## 6. 调度与事件

- Quartz/XXL-JOB 定时扫描 `posts` 中 `status=draft` 且 `publish_at <= now` 的记录，执行发布。
- 事件：`content.post.created/updated/published/deleted`。payload 含 slug、authorId、publishAt。
- 下游：SEO Service（更新站点地图）、Notification（订阅提醒）、Analytics（PV 初始化）、BFF（缓存刷新）。

## 7. 规则与校验

- 同一 tenant slug 唯一；自动生成 slug（`title -> transliterate -> uniqueness`）。
- 文章内容最大 200KB Markdown，超出需拆分/附件。
- 多语言：`post_translations` 记录 `source_post_id` 与 `language`。
- 审核配置：若启用 Flow，则 `status=pending` 时需管理员审批。

## 8. 依赖

- Auth Service：校验作者权限。
- Media Service：封面/插图引用（`cover_media_id`，需存在）。
- Search：通过事件监听同步到 Elasticsearch。
- Notification：接收 `published` 事件发送订阅邮件。

## 9. 测试要点

- [ ] slug 冲突处理，自动追加 `-1/-2`
- [ ] 定时发布任务幂等（重复执行不多发）
- [ ] 版本回滚保持版本链正确
- [ ] 多语言文章互相关联
- [ ] 删除文章时同步删除标签关联 & 发送事件
