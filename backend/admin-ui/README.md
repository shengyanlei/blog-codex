# Codex Blog 管理端（Static Admin UI）

该目录提供一个纯静态版本的管理端 Demo，帮助快速验证后端 API。打开 `index.html` 即可看到登录界面和基础管理操作。

## 功能概览

- 管理员登录（调用 `/api/v1/auth/login` 获取 token）
- 仪表盘展示指标（当前为占位数据，可对接 Analytics API）
- 文章管理：创建文章、拉取文章列表、触发发布
- 媒体上传入口（示例提示，需接入 Media Service 上传策略）
- 用户/配置区域留空，方便后续扩展

## 使用方式

1. 启动后端相关服务（Auth、Content、Admin 等），确保 API 可访问。
2. 使用静态服务器或直接通过浏览器打开 `index.html`。
   - 推荐命令：`npx serve backend/admin-ui`
3. 在登录框输入管理员账号（示例 `admin@example.com`），调用后端获取 token。
4. 登录后可进行文章创建/发布、查看概览等操作。

> 注意：该 Demo 仅为快速原型，未集成路由/构建工具，不包含复杂状态管理。实际项目可使用 React/Vue + Ant Design Pro 等框架实现完整的管理台，再复用 API。
