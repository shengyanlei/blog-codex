# Blog Codex

Blog Codex 是一个现代化的博客系统，采用前后端分离架构。后端基于 Java Spring Boot 构建微服务，前端使用 React + Vite 构建高性能用户界面。

## 架构概览

- **Frontend**: React, Vite, TailwindCSS (计划中)
- **Backend**: Java 11+, Spring Boot 3, Spring Cloud, MySQL, Redis

## 快速开始

### 前端 (Frontend)

请确保已安装 Node.js (推荐 v18+)。

```bash
cd frontend
npm install
npm run dev
```

详细说明请参考 [Frontend README](./frontend/README.md)。

### 后端 (Backend)

请确保已安装 Java 11+ 和 Maven。

```bash
cd backend
mvn clean install
# 启动各个服务，例如：
mvn -pl auth-service spring-boot:run
```

详细说明请参考 [Backend README](./backend/README.md)。

## 文档

- [需求文档](./需求文档/blog-system-requirements.md)
- [后端技术设计](./需求文档/backend-technical-design.md)
- [后端接口文档](./backend/api-spec.md)
