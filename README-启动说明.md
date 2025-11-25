# Blog Codex 启动说明

## 一键启动

### Windows 用户

#### 方式一：使用批处理脚本（推荐）
双击运行 `start-all.bat` 即可启动所有服务。

```bash
start-all.bat
```

#### 方式二：使用 PowerShell 脚本
右键点击 `start-all.ps1`，选择"使用 PowerShell 运行"。

或在 PowerShell 中执行：
```powershell
.\start-all.ps1
```

> **注意**：首次运行 PowerShell 脚本可能需要修改执行策略：
> ```powershell
> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
> ```

### 停止所有服务

双击运行 `stop-all.bat` 即可停止所有服务。

```bash
stop-all.bat
```

## 服务端口

启动后，各服务访问地址如下：

| 服务 | 端口 | 访问地址 |
|------|------|----------|
| Gateway | 8080 | http://localhost:8080 |
| Auth Service | 8081 | http://localhost:8081 |
| Content Service | 8082 | http://localhost:8082 |
| Frontend | 5173 | http://localhost:5173 |

## 环境要求

- **JDK**: 11 或更高版本
- **Node.js**: 16 或更高版本
- **Maven**: 3.6 或更高版本（通常随 IDE 安装）
- **数据库**: MySQL 8.0（需提前启动并配置）

## 手动启动（可选）

如果一键启动脚本遇到问题，可以手动启动各服务：

### 后端服务

在 `backend` 目录下分别启动：

```bash
# Gateway
mvn spring-boot:run -pl gateway -am

# Auth Service
mvn spring-boot:run -pl auth-service -am

# Content Service
mvn spring-boot:run -pl content-service -am
```

### 前端服务

在 `frontend` 目录下：

```bash
npm install  # 首次运行需要安装依赖
npm run dev
```

## 常见问题

### 1. 端口被占用

如果提示端口被占用，可以：
- 运行 `stop-all.bat` 停止所有服务
- 手动查找并关闭占用端口的进程

查看端口占用（Windows）：
```bash
netstat -ano | findstr :8080
taskkill /F /PID <进程ID>
```

### 2. Maven 构建失败

- 检查网络连接，确保可以访问 Maven 中央仓库
- 尝试使用国内镜像源（阿里云 Maven 镜像）
- 清理 Maven 缓存：`mvn clean`

### 3. 前端启动失败

- 确保已安装依赖：`npm install`
- 清理缓存：`npm cache clean --force`
- 删除 `node_modules` 和 `package-lock.json` 后重新安装

### 4. 数据库连接失败

- 确保 MySQL 服务已启动
- 检查 `application.yml` 中的数据库配置
- 确认数据库用户名和密码正确

## 开发建议

1. **首次启动**：建议先手动启动各服务，确保配置正确
2. **日志查看**：启动脚本会为每个服务打开独立窗口，方便查看日志
3. **热重载**：前端支持热重载，修改代码后自动刷新
4. **后端调试**：建议使用 IDE（如 IntelliJ IDEA）启动后端服务，方便调试

## 生产部署

生产环境建议使用 Docker Compose 或 Kubernetes 进行部署，而非使用这些开发脚本。
