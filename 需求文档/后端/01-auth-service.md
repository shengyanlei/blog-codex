# 账号与权限服务设计（Auth Service）

## 1. 模块目标

- 提供统一身份认证、授权与安全策略，覆盖邮箱/手机号注册、OAuth 第三方登录、多因素认证以及细粒度 RBAC。
- 面向 Java 11 + Spring Boot 3 实现，使用 Spring Security、Spring Authorization Server，数据落地 MySQL 8.0，Session/Token 信息存 Redis。

## 2. 业务边界

- **包含**：用户注册登录、密码策略、OAuth 绑定/解绑、用户信息查询、角色/权限管理、MFA、登录审计、API Token。
- **不包含**：内容数据操作、业务配置；只提供认证/鉴权接口与事件。

## 3. 核心流程

1. 用户注册：提交邮箱/手机号 + 验证码 → 校验 → 创建 `users` + 默认角色 → 发欢迎通知。
2. 登录：支持密码、短信验证码、OAuth，成功后签发 JWT（Access 15min、Refresh 7d）。
3. MFA：为需要提升安全等级的账号配置 TOTP，登录成功后追加验证码校验。
4. 授权：根据角色、权限组装 `Authorities`，Spring Security 网关进行校验。

## 4. 数据模型（MySQL）

- `users`：`id(PK)`, `email`, `phone`, `password_hash`, `status`, `last_login_at`, `mfa_secret`, `tenant_id`, `created_at`, `updated_at`
- `user_identities`：记录 OAuth 绑定信息（`provider`, `provider_uid`, `user_id`）
- `roles` / `permissions` / `role_permissions` / `user_roles`
- `login_audits`：`id`, `user_id`, `ip`, `device`, `result`, `fail_reason`, `created_at`
- `api_tokens`：`id`, `user_id`, `name`, `scopes`, `hashed_token`, `expired_at`, `last_used_at`

索引：`users.email`/`phone` 唯一；`user_identities (provider, provider_uid)` 唯一；`login_audits` 按 `created_at` 分区。

## 5. API 设计

| 场景 | Method & Path | Request | Response | 备注 |
| ---- | ------------- | ------- | -------- | ---- |
| 注册 | `POST /api/v1/auth/register` | `{email, phone?, password, verifyCode}` | `{userId}` | 验证码由通知服务发送；密码策略：>=10位，包含大小写与数字 |
| 登录 | `POST /api/v1/auth/login` | `{username, password}` | `{accessToken, refreshToken, mfaRequired}` | 登录失败 >5 次封禁 10 分钟 |
| OAuth 跳转 | `GET /api/v1/auth/oauth/{provider}/authorize` | - | 302 到第三方 | provider: github/google/wechat |
| OAuth 回调 | `POST /api/v1/auth/oauth/{provider}/callback` | 第三方 code | `{accessToken,...}` | 完成绑定或登录 |
| 启用 MFA | `POST /api/v1/auth/mfa/enable` | `{verifyCode}` | `{otpauthUrl}` | 返回密钥二维码 |
| 角色管理 | `POST /api/v1/roles` / `PUT /api/v1/roles/{id}` | `{name, permissions[]}` | `{id}` | 仅管理员 |
| API Token | `POST /api/v1/api-tokens` | `{name, scopes, expiredAt}` | `{token}` | 仅展示一次纯文本 token |

统一响应：`{ code, message, data, traceId }`，错误码前缀 `AUTH_`。

## 6. 事件与集成

- 事件：`auth.user.registered`, `auth.user.login.failed`, `auth.role.updated`, `auth.api_token.created`
- MQ Topic：`auth.events`（JSON，含 `tenantId`, `userId`, `payload`）
- 订阅方：通知服务（发送欢迎邮件/安全提醒）、审计服务。

## 7. 安全要求

- 密码 Hash 使用 Argon2id，盐值随机 16 字节。
- MFA 密钥使用 KMS 加密后存 `users.mfa_secret`。
- Refresh Token 存 Redis（`refresh:<tokenId>`），登出即失效。
- 防爆破：IP + 账号维度限流（Redis Lua 计数）。
- 管理接口需双重校验（角色 + 权限 + IP 白名单）。

## 8. 开发注意事项

- 统一使用 MapStruct / Record DTO。
- 与 API Gateway 约定 JWT Header `Authorization: Bearer`，TraceId 通过 `X-Request-Id` 传递。
- 单元测试覆盖：密码策略、MFA、OAuth mock；集成测试验证 token 流程。

## 9. 验证清单

- [ ] 注册/登录/忘记密码 happy path
- [ ] OAuth 绑定冲突处理
- [ ] JWT 过期刷新
- [ ] RBAC 权限拒绝正确返回 `403 AUTH_FORBIDDEN`
- [ ] 登录审计与安全通知写入 MQ
