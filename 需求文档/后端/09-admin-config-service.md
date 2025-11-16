# 运维与配置服务设计（Admin & Config Service）

## 1. 目标

- 管理站点级配置、主题、导航、插件状态、备份/恢复、灰度策略、权限审计等运维能力。

## 2. 功能范围

- 站点基础信息（名称、域名、语言、时区）
- 导航/菜单/页脚配置
- 主题/主题配置管理
- 插件安装/启用/卸载、版本管理
- 备份/恢复、灰度发布策略、审计日志查询

## 3. 数据模型（MySQL）

- `site_settings`: `id`, `tenant_id`, `key`, `value_json`, `version`, `updated_by`, `updated_at`
- `themes`: `id`, `tenant_id`, `name`, `version`, `config_schema`, `status`, `package_url`
- `theme_settings`: `id`, `theme_id`, `tenant_id`, `settings_json`
- `plugins`: `id`, `name`, `version`, `manifest_json`, `status`, `entrypoint`
- `plugin_instances`: `id`, `plugin_id`, `tenant_id`, `config_json`, `enabled`
- `backups`: `id`, `tenant_id`, `type(db/object/media)`, `location`, `status`, `created_at`
- `audit_logs`: `id`, `tenant_id`, `actor_id`, `action`, `resource`, `ip`, `payload`, `created_at`

## 4. API

| 功能 | Method & Path | Request | Response | 权限 |
| ---- | ------------- | ------- | -------- | ---- |
| 获取站点配置 | `GET /api/v1/admin/settings` | `?keys=site.name,site.lang` | `{key:value}` | 管理员 |
| 更新配置 | `PUT /api/v1/admin/settings/{key}` | `{value}` | `{version}` | 乐观锁 |
| 主题管理 | `POST /api/v1/admin/themes` 上传 | `{name, version, package}` | `{themeId}` | |
| 切换主题 | `POST /api/v1/admin/themes/{id}/activate` | `{settings}` | `{status}` | |
| 插件安装 | `POST /api/v1/admin/plugins` | `{manifestUrl}` | `{pluginId}` | 包含签名校验 |
| 插件启用/停用 | `POST /api/v1/admin/plugins/{id}/toggle` | `{enabled}` | `{status}` | |
| 备份 | `POST /api/v1/admin/backups` | `{types}` | `{backupId}` | 异步执行 |
| 恢复 | `POST /api/v1/admin/backups/{id}/restore` | `{phase}` | `{status}` | 需二次确认 |
| 灰度配置 | `PUT /api/v1/admin/deployments/{id}` | `{strategy, percent, startAt}` | `{status}` | |
| 审计查询 | `GET /api/v1/admin/audit-logs` | `?actor&action&from&to` | `Paged<AuditLog>` | 只读 |

## 5. 灰度/发布流程

- 通过策略配置（蓝绿、金丝雀）：定义 `traffic_percent`, `criteria`.
- 与 Service Mesh (Istio) 集成：Admin Service 更新 CRDs/ConfigMap。
- 支持回滚按钮：触发自动恢复上一个稳定版本。

## 6. 备份/恢复

- 备份流程：触发 → 生成任务（数据库使用 XtraBackup，媒体通过对象存储生命周期）→ 完成后记录 location。
- 恢复流程：只允许管理员执行，需输入确认词；执行前自动备份当前状态。

## 7. 审计

- 所有敏感操作（配置修改、主题切换、插件操作、恢复等）写入 `audit_logs`。
- 以事件形式发送 `admin.audit.logged` 到安全系统。

## 8. 安全

- 管理接口需要 IP 白名单 + MFA。
- 配置值加密存储（如 API Keys 使用 KMS）。
- 插件安装需校验签名 + 扫描 manifest，禁止访问受限资源。

## 9. 测试

- [ ] 配置更新并发冲突（版本号）处理
- [ ] 主题切换后 BFF 缓存失效
- [ ] 插件启停触发生命周期事件
- [ ] 备份失败回滚 & 通知
- [ ] 灰度策略生效（百分比流量验证）
