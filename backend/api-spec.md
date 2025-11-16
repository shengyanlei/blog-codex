# Codex Blog 后端接口文档（v1）

说明：

- 所有接口走 `https://{host}/api/v1` 前缀（BFF/Gateway 统一转发）。
- 鉴权：除公开查询外，需携带 `Authorization: Bearer <JWT>`。
- 统一返回格式：`{ "code": "OK", "message": "", "data": {}, "traceId": "" }`。
- 错误码以模块前缀区分。例如：`AUTH_401_INVALID_CREDENTIALS`。

## 1. 认证（Auth Service）

### 1.1 注册

```
POST /api/v1/auth/register
Content-Type: application/json
Body: {
  "email": "user@example.com",
  "password": "Secret1234!",
  "verifyCode": "812345",
  "nickname": "Coder"
}
```

Response:
```
{
  "code": "OK",
  "data": { "userId": "u_12345" }
}
```

### 1.2 登录

```
POST /api/v1/auth/login
Body: { "username": "user@example.com", "password": "Secret1234!" }
```

Response:
```
{
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "mfaRequired": false,
    "expiresIn": 900
  }
}
```

### 1.3 刷新 Token
`POST /api/v1/auth/token/refresh`，Body: `{ "refreshToken": "..." }`

### 1.4 角色管理

```
POST /api/v1/roles
Body: { "name": "editor", "permissions": ["post:read","post:approve"] }
```

## 2. 内容管理（Content Service）

### 2.1 创建文章

```
POST /api/v1/posts
Body: {
  "title": "Hello World",
  "contentMd": "## Intro",
  "tags": ["java"],
  "categories": ["tech"],
  "language": "zh-CN",
  "publishAt": null
}
```

Response：`{ "data": { "id": "post_123", "status": "draft", "version": 1 } }`

### 2.2 更新文章
`PUT /api/v1/posts/{id}`，Body 包含可选字段 `title/contentMd/excerpt/metadata`.

### 2.3 查询文章
`GET /api/v1/posts/{id}?includeDraft=false`

### 2.4 发布文章
`POST /api/v1/posts/{id}/publish`，Body: `{ "publishAt": "2025-11-16T10:00:00Z" }`

### 2.5 标签管理
`GET /api/v1/tags?page=1&size=20`

## 3. 媒体管理（Media Service）

### 3.1 获取上传策略

```
POST /api/v1/media/upload-policy
Body: { "fileName": "banner.png", "mimeType": "image/png", "size": 2048000 }
```
Response: `{ "data": { "uploadId": "up_123", "url": "https://s3/...", "headers": {...}, "expireAt": 1741231 } }`

### 3.2 上传完成回调
`POST /api/v1/media/confirm`，Body: `{ "uploadId": "up_123", "hash": "md5", "width": 1200, "height": 630 }`

### 3.3 查询媒体
`GET /api/v1/media?page=1&size=20&keyword=cover`

## 4. 评论与互动（Interaction Service）

### 4.1 创建评论
`POST /api/v1/posts/{postId}/comments` Body: `{ "parentId": null, "content": "写得棒！" }`

### 4.2 拉取评论
`GET /api/v1/posts/{postId}/comments?cursor=&size=20&sort=new`

### 4.3 点赞
`POST /api/v1/comments/{id}/like` Body: `{ "like": true }`

### 4.4 收藏文章
`POST /api/v1/posts/{id}/favorite` Body: `{ "favorite": true }`

## 5. 通知与订阅（Notification Service）

### 5.1 查询未读
`GET /api/v1/notifications/unread?page=1&size=20`

### 5.2 标记已读
`POST /api/v1/notifications/{id}/read`

### 5.3 更新订阅偏好
`PUT /api/v1/subscriptions/{eventType}` Body: `{ "channel": "email", "enabled": true }`

## 6. SEO 与分享（SEO Service）

### 6.1 获取文章 Meta
`GET /api/v1/seo/posts/{slug}`

### 6.2 更新模板
`PUT /api/v1/seo/templates/post` Body: `{ "titleFormat": "{{title}} - Codex Blog", "description": "{{excerpt}}" }`

### 6.3 Sitemap / RSS
- `GET /api/v1/seo/sitemap.xml`
- `GET /api/v1/seo/rss.xml?locale=zh-CN`

## 7. 统计与分析（Analytics Service）

### 7.1 查询文章指标
`GET /api/v1/analytics/posts/{postId}?from=2025-11-01&to=2025-11-16&granularity=daily`

### 7.2 热门文章
`GET /api/v1/analytics/posts/hot?range=24h`

### 7.3 导出报表
`POST /api/v1/analytics/export` Body: `{ "type": "post-metrics", "filters": {...}, "format": "csv" }`

## 8. 运维与配置（Admin Service）

### 8.1 获取站点配置
`GET /api/v1/admin/settings?keys=site.name,site.lang`

### 8.2 更新配置
`PUT /api/v1/admin/settings/site.name` Body: `{ "value": "Codex Blog" }`

### 8.3 主题管理
`POST /api/v1/admin/themes` （multipart 上传 zip）

### 8.4 插件启停
`POST /api/v1/admin/plugins/{id}/toggle` Body: `{ "enabled": true }`

### 8.5 备份与恢复
- `POST /api/v1/admin/backups` Body: `{ "types": ["db","media"] }`
- `POST /api/v1/admin/backups/{id}/restore` Body: `{ "phase": "confirm" }`

## 9. 插件生态（Plugin Service）

### 9.1 注册 Webhook
`POST /api/v1/plugins/webhooks` Body: `{ "url": "https://plugin.dev/hook", "events": ["content.post.published"], "secret": "xxx" }`

### 9.2 获取插件 Token
`POST /api/v1/plugins/tokens` Body: `{ "scopes": ["content.read"], "expiresIn": 3600 }`

### 9.3 Hook 回调（平台 -> 插件）

```
POST https://plugin.dev/hook
Headers:
  X-Codex-Event: content.post.beforePublish
  X-Codex-Signature: sha256=...
Body:
{
  "postId": "post_123",
  "payload": {...}
}
```

## 10. BFF / Gateway

### 10.1 首页聚合
`GET /bff/v1/home?page=1&size=10&locale=zh-CN`

Response：
```
{
  "data": {
    "featured": [...],
    "feed": {
      "items": [...],
      "nextCursor": "..."
    },
    "sidebar": {
      "popular": [...],
      "announcement": {...}
    },
    "themeBlocks": {...}
  }
}
```

### 10.2 GraphQL 查询

```
POST /bff/graphql
Body:
{
  "query": "query Feed($page:Int!){ feed(page:$page,size:10){ items { id title excerpt author { name } } } }",
  "variables": { "page": 1 }
}
```

---

如需更细的字段定义或错误码列表，可在对应服务代码中通过 `springdoc-openapi` 自动生成。
