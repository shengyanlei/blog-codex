# BFF / 内容分发服务设计（Content Delivery BFF）

## 1. 目标

- 面向 Web/PWA/移动端提供聚合 API，统一鉴权、缓存、灰度控制，并对接主题配置。
- 使用 Spring Boot + GraphQL Java + Spring WebFlux，实现高并发低延迟。

## 2. 职责

- 聚合 Content、Interaction、Notification、Admin 配置。
- 处理公共查询（首页流、归档、搜索、作者页）。
- 提供匿名访问能力，应用缓存/CDN。
- 暴露 GraphQL schema 给插件/前端；同时提供 REST fallback。

## 3. 数据流

1. 客户端发起请求 → BFF 验证 JWT/访客 token。
2. BFF 调用下游服务（gRPC/REST），并将常访问数据缓存 Redis（key 包含 tenant + path + locale）。
3. 收敛结果 -> 应用主题配置 -> 输出 DTO。

## 4. API

### REST 示例

| 功能 | Method & Path | Params | 数据来源 | 缓存 |
| ---- | ------------- | ------ | -------- | ---- |
| 首页流 | `GET /bff/v1/home` | `?page&size&locale` | Content(文章) + Interaction(热门) + Admin(公告) | Redis 30s |
| 文章详情 | `GET /bff/v1/posts/{slug}` | `?preview=false` | Content + Interaction + Media | Redis 10s，preview 禁缓存 |
| 标签页 | `GET /bff/v1/tags/{tag}/posts` | `?page` | Content | 30s |
| 搜索 | `GET /bff/v1/search` | `?q&page` | Elasticsearch | 不缓存 |
| 作者主页 | `GET /bff/v1/authors/{id}` |  | Auth + Content + Interaction | 60s |

### GraphQL（片段）

```graphql
type Query {
  post(slug: String!, preview: Boolean): Post
  feed(page: Int!, size: Int!, locale: String): FeedPage
  author(id: ID!): Author
}
```

## 5. 缓存策略

- Redis Hash + KeySet，key 结构：`bff:{tenant}:{locale}:{route}:{paramsHash}`。
- 支持主动失效：监听 `content.post.published` / `config.updated` 事件，删除相关 key。
- CDN：GET 接口设置 `Cache-Control: public, max-age=30`，配合 ETag。

## 6. 主题/配置注入

- 从 Admin Service 获取 `theme_settings`，包括 Banner、侧边组件、国际化词条。
- BFF 在响应中附带 `themeBlocks`，供前端渲染。

## 7. 错误与降级

- 调用下游失败：快速失败 + Fallback（例如 Interaction 返回空列表）。
- 超时设置：`WebClient` 超时 800ms，重试 1 次。
- 限流：对 IP/用户进行速率限制（Redis + token bucket）。

## 8. 安全

- 公共接口支持匿名 token，写操作统一走网关，不在 BFF 开放。
- 防止缓存穿透：对不存在的 slug 缓存短期空值（10s）。
- GraphQL Depth/Complexity 限制，防止恶意查询。

## 9. 测试

- [ ] 缓存命中/失效策略
- [ ] GraphQL schema 字段权限
- [ ] 搜索接口分页与高亮
- [ ] 主题配置切换实时生效
- [ ] 灰度流量：基于 header 划分不同主题输出
