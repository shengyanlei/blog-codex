# 通知与订阅服务设计（Notification Service）

## 1. 目标

- 统一管理站内信、邮件、Webhook、实时推送、订阅（RSS/Newsletter），确保可靠投递与偏好控制。

## 2. 数据模型

- `notification_templates`: `id`, `channel(email/web/webhook)`, `name`, `locale`, `subject`, `content`, `variables`, `version`
- `notifications`: `id`, `event_type`, `payload_json`, `status`, `retry_count`, `created_at`
- `notification_deliveries`: `id`, `notification_id`, `channel`, `recipient`, `status`, `last_error`, `sent_at`
- `subscription_preferences`: `id`, `user_id`, `channel`, `event_type`, `enabled`
- `webhook_endpoints`: `id`, `owner_id`, `url`, `secret`, `events`, `status`

## 3. 事件驱动

- 订阅 MQ Topic：`content.post.published`, `interaction.comment.created`, `auth.user.registered`, `system.alert`.
- 收到事件后根据 `subscription_preferences` 生成通知任务写入队列。
- Worker 处理具体渠道发送，失败采用指数退避重试（最多 5 次）。

## 4. 渠道实现

- **站内信**：写入 `notifications` + `notification_deliveries`，BFF 查询未读消息。
- **邮件**：集成 SendGrid/Mailgun；模板渲染使用 Thymeleaf；记录 MessageId。
- **Webhook**：签名 `X-Codex-Signature=HMAC-SHA256(secret, payload)`，失败重试 + 死信队列。
- **实时推送**：写入 Redis Stream，WebSocket Gateway 消费发送。
- **Newsletter**：批量任务（每日/每周），生成内容 -> 调用邮件渠道。

## 5. API

| 功能 | Method & Path | Request | Response | 说明 |
| ---- | ------------- | ------- | -------- | ---- |
| 查询未读 | `GET /api/v1/notifications/unread` | `?page&size` | `Paged<Notification>` | |
| 标记已读 | `POST /api/v1/notifications/{id}/read` | - | `{status}` | |
| 管理模板 | `POST/PUT /api/v1/notification-templates` | `{channel, locale, subject, content}` | `{id}` | |
| 订阅设置 | `PUT /api/v1/subscriptions/{eventType}` | `{channel, enabled}` | `{status}` | |
| Webhook CRUD | `/api/v1/webhooks` | `{url, events, secret}` | `{id}` | 校验 URL |

## 6. 安全

- 邮件发送限制：单用户/日 < 200 封，Redis 计数。
- Webhook 签名校验 + IP 白名单可配置。
- 模板渲染防止 XSS（变量转义）。

## 7. 依赖

- Auth：用户信息、偏好默认值。
- Content/Interaction：事件来源。
- Admin：站点配置（邮件抬头、发件人）。

## 8. 测试

- [ ] 模板变量缺失时错误提示
- [ ] 重试策略生效（3 次失败进入死信）
- [ ] Webhook 签名验证
- [ ] Newsletter 退订流程
- [ ] 通知偏好覆盖默认配置
