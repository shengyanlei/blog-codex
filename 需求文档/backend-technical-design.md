
# Codex Blog 后端技术方案

## 1. 设计目标

- 支撑《博客系统需求文档》中定义的多角色创作、内容管理、互动、统计与生态扩展能力。
- 保证高可用、高扩展、可插拔，便于多环境部署（自托管、SaaS）。
- 提供清晰的模块边界与接口契约，方便前端/移动端、插件和第三方服务集成。

## 2. 技术栈与基础设施

| 层级           | 方案                                                                 |
| -------------- | -------------------------------------------------------------------- |
| 语言与框架     | Java 11 + Spring Boot 3（结合 Spring Cloud / Spring Security）        |
| API 协议       | RESTful + WebSocket（实时通知）+ GraphQL（选配）                      |
| 数据库         | MySQL 8.0（主从 + 读写分离）；Redis 7（缓存 / 会话 / 限流）          |
| 对象存储       | S3 兼容接口（MinIO、本地 FS、OSS、COS 可插拔）                        |
| 消息队列       | RabbitMQ / Kafka（通知、异步任务、审计日志）                          |
| 搜索与统计     | Elasticsearch + ClickHouse（行为埋点）                                |
| 鉴权           | JWT + OAuth2（第三方登录）；支持 Keycloak / 自建 IdP                 |
| 部署           | Docker/K8s；CI/CD（GitHub Actions + ArgoCD）；配置中心（Consul 等）   |

## 3. 总体架构

```
[Client] -> [API Gateway / BFF] -> [Service Mesh (可选)]
                                -> Auth Service
                                -> Content Service
                                -> Media Service
                                -> Interaction Service
                                -> Notification Service
                                -> Analytics Service
                                -> Admin/Config Service

Shared: MySQL, Redis, Object Storage, MQ, Search, Observability Stack
```

- **API Gateway/BFF**：统一鉴权、限流、聚合接口，向 Web/PWA/Plugin 提供 REST/GraphQL。
- **Service Mesh**（可选）：Istio 管控灰度、流量治理。
- **Observability**：Prometheus + Grafana（监控）、ELK（日志）、Jaeger（链路）。

## 4. 功能拆解与实现方案

### 4.1 账号与权限（Auth Service）

- **功能**：注册登录、第三方 OAuth、MFA、角色/权限管理、会话与安全策略。
- **实现**：
  - 使用 Spring Boot + Spring Security OAuth2，实现 JWT Access/Refresh；Session 存 Redis。
  - OAuth 流程：Gateway 发起，Auth Service 处理回调、绑定用户。
  - 权限模型：RBAC（角色-权限-资源）；支持自定义角色。ACL 存于 `user_roles`、`role_permissions`。
  - IP/设备限制：在 Redis 记录登录历史，违反策略时触发风险控制。
  - MFA：TOTP（Google Authenticator），密钥经 KMS 加密后存 MySQL。

### 4.2 内容管理（Content Service）

- **功能**：文章 CRUD、草稿、版本、发布流程、标签/分类、多语言、定时任务。
- **实现**：
  - 数据库表：`posts`, `post_versions`, `post_translations`, `tags`, `categories`, `post_tag`.
  - 编辑器数据存 Markdown + JSON 元数据，渲染为 HTML 由前端或服务端完成。
  - 版本管理：每次保存记录 diff（存储 gzipped JSON），支持回滚和对比。
  - 发布队列：定时发布由 Scheduler（基于 Quartz / XXL-JOB）监听 `publish_at` 字段。
  - 审核流（可选）：`post_workflows` 配置状态机，支持多级审批。

### 4.3 媒体管理（Media Service）

- **功能**：文件上传/下载、处理（压缩、裁剪）、引用统计、防盗链。
- **实现**：
  - 上传走分块直传至对象存储，后端生成签名 URL。
  - 元数据表 `media_assets` 记录文件 hash、尺寸、归属、引用次数。
  - 使用 FFmpeg/ImageMagick 在异步任务中处理缩略图、转码，任务存 MQ。
  - CDN/防盗链：返回带 token 的临时 URL；配置 Referer 白名单。

### 4.4 前台展示支持（BFF + Content Service）

- **功能**：首页/详情/列表 API、搜索、推荐、主题配置。
- **实现**：
  - BFF 聚合 Content/Interaction/Config 数据，输出 Article DTO。
  - 搜索：写入 Elasticsearch，提供全文 + 权重排序；支持多语言 analyzer。
  - 推荐：基于标签、阅读记录、手动置顶（`featured_posts`）。复杂推荐可接入外部服务。
  - 主题配置：`site_themes`, `theme_settings` 表；通过 KV 输出给前端。

### 4.5 评论与互动（Interaction Service）

- **功能**：评论、回复、点赞、收藏、举报、敏感词过滤。
- **实现**：
  - 数据表：`comments`, `comment_actions`, `likes`, `favorites`, `reports`.
  - 评论结构：使用 `path` 字段（Materialized Path 存 VARCHAR）或 `parent_id + depth` 管理楼层；按分页查询。
  - 审核：状态机（pending/approved/rejected）；自动过滤（DFA + 机器学习服务）。
  - 垃圾过滤：接入第三方（Akismet）或自建模型，消息进 MQ 异步审核。
  - 点赞/收藏计数存 Redis + 定期批量回写 MySQL。

### 4.6 SEO 与分享（SEO Service）

- **功能**：生成 slug、Meta、站点地图、RSS、OG 数据、结构化数据。
- **实现**：
  - Slug 生成使用 transliteration + 唯一性校验。
  - Sitemap/RSS 由定时任务更新静态文件，存储在对象存储或 CDN。
  - Meta/OG 模板配置在 `seo_configs`；文章级覆盖来自 `post_metadata`。
  - JSON-LD Schema 模板根据内容类型（Article/Breadcrumb）。

### 4.7 通知与订阅（Notification Service）

- **功能**：站内消息、邮件、Webhook、实时推送、RSS/Newsletter。
- **实现**：
  - 事件驱动：各服务往 MQ 推送 Domain Event（如 `comment.created`）。
  - Notification Service 订阅事件，根据用户偏好表 `notification_preferences` 发送。
  - 邮件：使用 Mailgun/SES；模版存 `notification_templates`；任务异步重试。
  - Webhook：可配置回调 URL、签名、失败重试策略（指数退避）。
  - 实时：WebSocket/Server-Sent Events 推送未读消息。

### 4.8 统计与分析（Analytics Service）

- **功能**：PV/UV、行为埋点、热门文章、来源分析、报表导出。
- **实现**：
  - 前端 SDK 上报事件到 Kafka，Fluent Bit 写入 ClickHouse。
  - 实时指标：Redis HyperLogLog 记录 UV，定时写 ClickHouse。
  - 提供聚合 API（文章热度、阅读时长），导出 CSV 由异步任务生成后发送下载链接。
  - 与 Elasticsearch 联动实现站内搜索词统计。

### 4.9 运维与配置（Admin Service）

- **功能**：站点配置、主题/插件、备份/恢复、灰度发布、权限审计。
- **实现**：
  - 配置中心：`site_settings` + Consul；支持热更新与回滚。
  - 插件：上传 zip -> 扫描 manifest -> 执行安装脚本 -> 托管于容器或沙箱（WebAssembly/Node VM）。
  - 备份：触发数据库 dump + 对象存储备份；恢复需管理员审批。
  - 灰度：通过服务网格或发布策略（蓝绿、金丝雀），配置在 `deployment_strategies`。
  - 审计：所有敏感操作记录到 `audit_logs`，并发送到安全分析系统。

### 4.10 扩展生态（Plugin/Theme API）

- **功能**：主题/插件生命周期、API Token、Webhooks。
- **实现**：
  - 插件沙箱：提供 SDK（HTTP + Webhook + GraphQL 订阅）限制权限；动态加载配置。
  - API Token：`api_keys` 表，支持作用域（内容读写、用户管理等），可设置失效时间。
  - Webhooks：订阅事件 -> 签名 -> 重试；对插件暴露同样机制，保障隔离。

## 5. 数据库模型概览

- MySQL 按业务领域拆分逻辑库或使用前缀(`auth_*`,`content_*`)，便于独立扩展。
- 关键表间关系：
  - `users` 1:N `posts`; `posts` N:M `tags`; `posts` 1:N `comments`.
  - `media_assets` N:1 `users` (owner)；`post_media` 做关联。
  - `notifications` 1:N `notification_deliveries`。
- 对大字段（内容、版本 diff）使用 MySQL LONGTEXT/JSON 或拆至对象存储，表内存引用。
- 审计日志、报表等冷数据定期归档（partition + TTL）。

## 6. API 设计原则

- RESTful 命名：`/api/v1/posts`, `/api/v1/posts/{id}/comments`。
- 标准响应：`{ code, message, data, traceId }`；错误码采用模块前缀。
- 分页：Cursor/Offset 兼容；默认 20，最大 100。
- 变更操作需 CSRF 保护（Cookie + Header Token）。
- GraphQL（可选）向前端提供自定义查询，底层复用 Service Layer。

### 6.1 样例 API

#### 创建文章

```
POST /api/v1/posts
Authorization: Bearer <token>
Body:
{
  "title": "Spring Boot 模块化最佳实践",
  "content": "...Markdown...",
  "tags": ["Spring Boot", "Architecture"],
  "status": "draft",
  "publishAt": "2024-09-01T10:00:00Z"
}
```

#### 拉取评论

```
GET /api/v1/posts/{id}/comments?cursor=xxx&size=20
Response:
{
  "data": [
    {
      "id": "c123",
      "content": "写得太棒了",
      "author": {...},
      "path": "0001.0003",
      "actions": { "likeCount": 10, "liked": true }
    }
  ],
  "nextCursor": "c999"
}
```

## 7. 缓存与性能

- Redis 层：
  - 会话、验证码、限流计数器。
  - 热点文章、标签、配置缓存（TTL + 主动失效）。
  - 异步任务队列（Redis Stream + Redisson / RocketMQ）与分布式锁。
- HTTP 层：
  - CDN 缓存静态资源、公开 API（GET）。
  - 应用层 ETag / Last-Modified，配合 Conditional Request。
- 数据库优化：
  - 常用查询加复合索引（如 `posts(status, publish_at)`）。
  - 大表分区（按月份），减轻归档压力。

## 8. 安全与合规

- 全站 HTTPS，HSTS，TLS1.2+。
- 输入校验、XSS/CSRF/SQL 注入防护（框架 + WAF）。
- 密码与敏感数据使用 Argon2 + KMS；密钥集中管理。
- 权限审计日志不可删改，写入独立审计库。
- 内容审核：AI + 人工，支持多地区合规（GDPR、数据本地化）。

## 9. 部署与运维

- 环境：dev/staging/prod，多租户配置隔离。
- CI/CD：
  - Lint/Test -> Build 镜像 -> 安全扫描 -> 推送 Registry -> ArgoCD 部署。
  - 基础迁移：Liquibase/Flyway 自动执行，失败自动回滚。
- 灾备：
- MySQL 异地备份（XtraBackup/BR）+ Binlog PITR；对象存储版本化。
  - 定期演练恢复；RPO < 5min，RTO < 30min。
- 监控：
  - 指标：QPS、RT、错误率、队列长度、DB 连接、缓存命中率。
  - 告警：Prometheus Alertmanager -> Slack/钉钉。

## 10. 开发计划（后端视角）

| Sprint | 交付内容                                                                                                    |
| ------ | ----------------------------------------------------------------------------------------------------------- |
| S1     | 骨架搭建（Spring Boot/Go）、基础 Auth、Post CRUD、ORM & Migration、CI/CD 基础                                |
| S2     | 媒体上传、评论、通知事件总线、Redis 缓存、站点配置                                                          |
| S3     | 搜索/推荐、统计埋点管道、SE0 服务、Webhook/插件 API、灰度/备份流程                                          |
| S4     | 安全加固（MFA、审计）、多租户能力、性能优化、可观测性、扩展接口（GraphQL、实时推送）                        |

## 11. 风险与缓解

- **多租户隔离**：使用 schema + tenantId；需要定期审计 SQL，预防跨租户访问。
- **插件安全**：沙箱执行 + 权限声明 + 人工审核，提供 API 限速。
- **媒体与大文件**：防止占用磁盘 -> 对象存储 + 生命周期管理；转码任务拆小粒度。
- **统计链路**：实时性 vs 成本 -> 热数据 Redis，冷数据 ClickHouse。
- **第三方依赖**：邮件、OAuth 等需降级策略（队列重试、备用服务）。

## 12. 工程结构与构建

- **构建工具**：统一采用 Maven 3.9+，父 POM `codex-blog-backend/pom.xml` 继承 Spring Boot 3.3.5，并通过 `spring-cloud-dependencies` 管理 Cloud 版本。
- **模块划分**：`common`（共享 DTO/工具）+ `gateway`（BFF）+ 九个领域服务（auth/content/media/interaction/notification/seo/analytics/admin/plugin），所有模块以 `jar` 形式发布。
- **依赖复用**：父 POM 中统一声明 Web、JPA、Redis、AMQP 等 starter 版本，MapStruct 作为 annotation processor，并通过 `<modules>` 控制构建顺序。
- **构建命令**：本地执行 `mvn clean install` 或 `mvn -pl <module> spring-boot:run` 进行单服务启动；CI 中使用 `mvn verify -T4` 并缓存本地仓库，加速多模块编译。
- **产物发布**：各服务打包为可执行 jar，镜像构建阶段基于 `eclipse-temurin:11-jre`，保持与 Maven 输出一致；同时生成 `api-spec` 供文档同步。

---

此方案可作为后端团队实现与评审的基础，落地时可进一步细化模块级接口、ER 图、部署脚本以及 SLA/容量评估。
