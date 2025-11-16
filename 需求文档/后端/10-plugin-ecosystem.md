# 插件与生态服务设计（Plugin Ecosystem）

## 1. 目标

- 构建安全可控的插件/主题生态，支持安装、启用、事件订阅、生命周期管理、API Token 授权。

## 2. 架构

- 插件管理中心（Admin Service 内部模块）+ 插件运行容器（Node/Java/Wasmer）。
- 提供 SDK（HTTP/GraphQL/Webhook），通过 API Gateway 访问，采用 Scope 限制。

## 3. 插件模型

- `plugin_manifest`（JSON）包含：`id`, `name`, `version`, `description`, `entrypoint`, `scopes`, `permissions`, `hooks`, `installScripts`.
- 插件可选择：
  - **前端型**：仅注入 JS/CSS。
  - **服务型**：运行在独立容器，暴露 HTTP Endpoint。
  - **Webhook 型**：监听事件。

## 4. 生命周期

1. 上传 manifest/包 → 校验签名、依赖、兼容版本。
2. 安装：执行 `installScripts`（数据库迁移、配置），注册 hooks。
3. 启用：分配 API Token + Webhook Secret，写入 `plugin_instances`.
4. 运行：插件通过 SDK 访问 `/api/v1/plugin/{id}/...`。
5. 更新：版本兼容性检测，执行 `upgradeScripts`。
6. 卸载：停用 → 清理资源 → 删除记录。

## 5. Hooks & API

| Hook | 描述 | 调用方式 |
| ---- | ---- | -------- |
| `content.post.beforePublish` | 文章发布前提供修改/校验 | HTTP POST 到插件回调 |
| `content.post.afterPublish` | 发布后通知 | 异步 |
| `interaction.comment.created` | 评论创建 | Webhook |
| `analytics.report.generated` | 报表生成完成 | Webhook |
| `admin.config.changed` | 配置变化 | HTTP/事件 |

插件可访问的 API：

- `GET /api/v1/plugin/content/posts/{id}`（需 `content.read` scope）
- `POST /api/v1/plugin/notifications` 发送站内提醒（需 `notify.send`）
- Webhook 事件统一由 Notification Service 投递。

## 6. 安全机制

- 插件运行沙箱：限制 CPU/内存、网络白名单，只能访问允许的内部服务。
- Scope-based Token：在安装时选择最小权限。
- 请求签名：插件调用平台 API 必须附带 `X-Plugin-Id` + `Authorization: Bearer`.
- 日志隔离：每个插件单独日志流。

## 7. 监控与审计

- 插件运行状态心跳，每 1 分钟上报。
- 指标：QPS、延迟、错误率、资源使用。
- 安全审计：插件调用敏感接口时记录 `audit_logs`。

## 8. 商城/分发

- 支持从官方市场下载：市场提供 API `GET /market/plugins`.
- 插件包签名验证（RSA），避免篡改。
- 评分/评论数据单独存储，供运营分析。

## 9. 测试

- [ ] Manifest 校验（必填字段、权限声明）
- [ ] Hook 超时重试逻辑
- [ ] Scope 限制生效（无权限返回 403）
- [ ] 升级回滚
- [ ] 插件卸载彻底清理配置
