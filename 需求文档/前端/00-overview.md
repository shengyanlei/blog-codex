# 00. 全局概览 (Overview)

## 1. 布局设计 (Layouts)

### 1.1 主布局 (MainLayout)
适用于博客展示、文章阅读等公开页面。
- **Header**: 
  - Logo (左侧)
  - 导航菜单 (首页, 分类, 标签, 关于)
  - 搜索栏 (右侧)
  - 用户头像/登录按钮 (右侧)
  - 主题切换开关 (日/夜)
- **Content**: 
  - 宽度限制 (max-w-7xl)
  - 响应式 Padding
- **Footer**: 
  - 版权信息
  - 社交链接
  - 备案号

### 1.2 认证布局 (AuthLayout)
适用于登录、注册页面。
- 居中卡片式设计
- 背景图/色
- 仅保留 Logo 和简单的返回首页链接

### 1.3 专注布局 (FocusLayout)
适用于编辑器页面。
- 隐藏顶部导航和底部 Footer
- 顶部仅保留操作栏 (发布, 保存, 设置, 返回)
- 最大化编辑区域

## 2. 路由规划 (Routing)

| 路径 | 组件/页面 | 权限 | 说明 |
| --- | --- | --- | --- |
| `/` | HomePage | Public | 首页文章流 |
| `/posts/:slug` | PostDetail | Public | 文章详情 |
| `/category/:name` | CategoryPage | Public | 分类归档 |
| `/login` | LoginPage | Guest | 登录 |
| `/register` | RegisterPage | Guest | 注册 |
| `/editor/new` | EditorPage | User | 新建文章 |
| `/editor/:id` | EditorPage | User | 编辑文章 |
| `/dashboard/*` | DashboardLayout | User | 用户中心 (嵌套路由) |

## 3. 全局状态 (Global State)

- **Theme**: `light` | `dark` | `system` (持久化到 localStorage)
- **User**: 当前登录用户信息 (ID, Avatar, Role)
- **Toast**: 全局消息通知队列

## 4. 交互规范 (Interaction)

- **Loading**: 
  - 页面级: 顶部进度条 (NProgress 风格)
  - 局部: 骨架屏 (Skeleton) 或 Spinner
- **Error**: 
  - API 错误: Toast 提示
  - 页面崩溃: ErrorBoundary 显示友好提示并提供刷新按钮
- **Feedback**: 
  - 按钮点击需有按压态 (active)
  - 链接悬停需有颜色变化 (hover)
