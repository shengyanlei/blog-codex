# 01. 认证模块 (Authentication)

## 1. 功能概述
处理用户身份验证，包括注册、登录、登出及 Token 管理。

## 2. 页面需求

### 2.1 登录页 (/login)
- **表单**:
  - 邮箱/用户名输入框
  - 密码输入框 (支持显示/隐藏)
  - "记住我" 复选框
- **操作**:
  - 提交按钮 (Loading 状态)
  - "忘记密码" 链接 -> 跳转重置页
  - "注册账号" 链接 -> 跳转注册页
  - 第三方登录按钮 (GitHub, Google)

### 2.2 注册页 (/register)
- **表单**:
  - 昵称
  - 邮箱 (需校验格式)
  - 验证码 (发送到邮箱)
  - 密码 (强度提示)
  - 确认密码
- **流程**:
  1. 输入邮箱 -> 点击发送验证码 (60s 倒计时)
  2. 填写完整信息 -> 提交注册
  3. 注册成功 -> 自动登录或跳转登录页

## 3. 逻辑需求

### 3.1 JWT 管理
- **Access Token**: 
  - 存储在内存 (Zustand Store) 或短效 Cookie
  - 每次 API 请求通过 Header 携带: `Authorization: Bearer <token>`
- **Refresh Token**:
  - 存储在 HttpOnly Cookie (由后端 Set-Cookie)
  - Access Token 过期时，Axios 拦截器自动请求 `/auth/refresh` 接口换取新 Token
  - 刷新失败则强制登出，跳转登录页

### 3.2 路由守卫 (AuthGuard)
- 创建 `<AuthGuard>` 高阶组件
- 包裹需要权限的路由 (如 `/editor`, `/dashboard`)
- 逻辑:
  - 检查是否有 Token
  - 无 Token -> 重定向到 `/login?redirect=...`
  - 有 Token -> 渲染子组件

### 3.3 登出
- 调用后端登出接口 (清除 Refresh Token)
- 清除前端 Store 中的用户信息
- 跳转回首页
