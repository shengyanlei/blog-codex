# 评论与互动服务设计（Interaction Service）

## 1. 目标

- 提供评论、回复、点赞、收藏、举报等互动能力，支持敏感词检测、垃圾过滤与审核流。

## 2. 数据模型

- `comments`: `id`, `tenant_id`, `post_id`, `parent_id`, `path`, `depth`, `content`, `content_html`, `status`, `author_id`, `ip`, `user_agent`, `likes_count`, `reports_count`, `created_at`, `updated_at`
- `comment_actions`: `{comment_id, user_id, action_type(like/favorite/report), value, created_at}`
- `favorites`: `id`, `user_id`, `post_id`, `created_at`
- `likes`: `id`, `user_id`, `entity_type`, `entity_id`, `created_at`
- `reports`: `id`, `entity_type`, `entity_id`, `reason_code`, `description`, `reporter_id`, `status`

索引：`comments(post_id, status, created_at)`；`comment_actions(comment_id, user_id)` 唯一。

## 3. 评论流程

1. 用户提交评论 → 校验登录/黑名单 → 触发敏感词过滤。
2. 若规则通过：`status=approved`；否则 `pending` 等待人工审核。
3. 写入 `comments`，异步更新 Redis 计数、通知作者。
4. 回复使用 `path` 保存（格式 `0001.0003`），便于排序。

## 4. API

| 功能 | Method & Path | Request | Response | 限制 |
| ---- | ------------- | ------- | -------- | ---- |
| 创建评论 | `POST /api/v1/posts/{postId}/comments` | `{parentId?, content}` | `{id, status}` | 60s 内最多 3 次 |
| 获取评论 | `GET /api/v1/posts/{postId}/comments` | `?cursor&size&sort=hot|new` | `Paged<Comment>` | 游标分页 |
| 点赞评论 | `POST /api/v1/comments/{id}/like` | `{like:true/false}` | `{likeCount}` | 幂等 |
| 收藏文章 | `POST /api/v1/posts/{id}/favorite` | `{favorite:true}` | `{favored}` | |
| 举报 | `POST /api/v1/reports` | `{entityType, entityId, reasonCode, detail}` | `{reportId}` | 需登录 |
| 审核评论 | `POST /api/v1/comments/{id}/moderate` | `{status, reason}` | `{status}` | 管理员 |

## 5. 敏感词/垃圾过滤

- 本地 DFA 词库 + AI 服务 HTTP API。
- 垃圾检测（SpamScore）：综合 IP、频率、文本相似度，超过阈值进入 `pending`。
- 对接外部服务（Akismet）时通过 MQ 异步回调结果。

## 6. 事件

- `interaction.comment.created`（payload：`commentId`, `postId`, `authorId`, `contentPreview`）
- `interaction.comment.moderated`
- `interaction.post.favorited`

消费者：通知服务（邮件/站内信）、统计服务（更新互动数据）、内容服务（评论计数）。

## 7. 缓存与计数

- Redis 维护 `post:{id}:comment_count`, `comment:{id}:likes`.
- 批量回写任务每 5 分钟执行，将增量写回 MySQL。
- 热门评论列表缓存 30 秒。

## 8. 安全

- IP 黑名单：存 Redis Set。
- 防刷：用户+IP 维度限流；同一内容 hash 拒绝重复发送。
- Markdown 渲染结果使用 OWASP Sanitizer，防止 XSS。

## 9. 测试

- [ ] 二级回复 path 排序正确
- [ ] 敏感词命中走审核
- [ ] 点赞幂等（重复点赞不会累加）
- [ ] 举报流程推动审核状态
- [ ] 评论删除后缓存和计数更新
