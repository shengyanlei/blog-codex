# Codex Blog 前端技术架构设计

## 1. 设计目标
构建一个高性能、可扩展、易维护的现代化博客前端。强调极致的用户体验（UX）、优秀的搜索引擎优化（SEO）和清晰的代码架构。

## 2. 技术选型

| 领域 | 选型 | 理由 |
| --- | --- | --- |
| **核心框架** | **React 18** | 生态成熟，并发特性支持高性能交互。 |
| **构建工具** | **Vite** | 极速的开发服务器和构建性能。 |
| **语言** | **JavaScript (ESNext)** | 灵活高效，配合 JSDoc 提供类型提示（或可选 TypeScript）。 |
| **样式方案** | **Vanilla CSS + CSS Modules** | 原生性能最好，无运行时开销，配合 CSS Variables 实现动态主题。 |
| **路由** | **React Router v6** | 标准路由解决方案，支持 Data API。 |
| **状态管理** | **Zustand** | 轻量级，API 简洁，无样板代码。 |
| **数据请求** | **Axios + React Query** | 强大的服务端状态管理，支持缓存、重试、乐观更新。 |
| **Markdown** | **React Markdown / MDX** | 安全且灵活的 Markdown 渲染。 |

## 3. 架构设计

采用 **Feature-Sliced Design (FSD)** 的简化变体，将代码按业务领域（Features）而非技术类型聚合。

### 3.1 目录结构

```
src/
├── app/                # 全局应用设置
│   ├── App.jsx         # 根组件
│   ├── router.jsx      # 路由配置
│   └── styles/         # 全局样式 (Reset, Variables)
├── assets/             # 静态资源 (Images, Fonts)
├── components/         # 通用 UI 组件库 (Buttons, Inputs, Modals)
│   ├── Button/
│   └── Card/
├── features/           # 业务功能模块
│   ├── auth/           # 认证模块 (Login, Register)
│   ├── blog/           # 博客展示 (PostList, PostDetail)
│   ├── editor/         # 编辑器模块
│   └── user/           # 用户中心
├── hooks/              # 通用 Hooks (useTheme, useDebounce)
├── lib/                # 第三方库配置 (axios, analytics)
├── utils/              # 工具函数 (date, validation)
└── main.jsx            # 入口文件
```

### 3.2 核心模块设计

#### 3.2.1 样式与主题系统
不依赖庞大的 UI 框架，自建轻量级设计系统。
- **Design Tokens**: 在 `:root` 中定义颜色、间距、排版变量。
- **Dark Mode**: 通过 `data-theme="dark"` 属性切换 CSS 变量值。
- **CSS Modules**: 组件级样式隔离，避免冲突。

#### 3.2.2 状态管理策略
- **Server State** (文章列表、用户信息): 使用 React Query 管理，自动处理缓存和同步。
- **Client State** (UI 状态、表单): 使用 React useState 或 Zustand (全局 UI 状态，如侧边栏开关)。

#### 3.2.3 路由与代码分割
- 使用 `React.lazy` 和 `Suspense` 对路由组件进行懒加载，减少首屏体积。
- 预加载关键资源。

## 4. 开发规范

- **Linting**: ESLint (React Recommended) + Stylelint.
- **Formatting**: Prettier.
- **Commit**: Conventional Commits 规范。
- **Naming**: 
  - 组件: PascalCase (e.g., `ArticleCard.jsx`)
  - 变量/函数: camelCase
  - 常量: SCREAMING_SNAKE_CASE

## 5. 性能与 SEO

- **SEO**: 使用 `react-helmet-async` 动态管理 `<title>` 和 `<meta>` 标签。
- **Lighthouse 指标优化**:
  - 图片使用 `srcset` 和 `loading="lazy"`。
  - 字体子集化与预加载。
  - 关键 CSS 内联（Vite 自动处理）。

## 6. 后续规划
- 引入单元测试 (Vitest + React Testing Library)。
- PWA 支持 (Vite PWA Plugin)。
