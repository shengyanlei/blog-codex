@echo off
chcp 65001 >nul
echo ========================================
echo   Blog Codex - 一键启动脚本
echo ========================================
echo.

:: 设置颜色
color 0A

:: 检查 Java 环境
echo [1/4] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java 环境，请先安装 JDK 11 或更高版本
    pause
    exit /b 1
)
echo [✓] Java 环境检查通过
echo.

:: 检查 Node.js 环境
echo [2/4] 检查 Node.js 环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js 环境，请先安装 Node.js
    pause
    exit /b 1
)
echo [✓] Node.js 环境检查通过
echo.

:: 启动后端服务
echo [3/4] 启动后端服务...
echo 正在启动后端微服务，请稍候...
cd /d "%~dp0backend"
start "Gateway" cmd /k "mvn spring-boot:run -pl gateway -am"
timeout /t 5 /nobreak >nul
start "Auth Service" cmd /k "mvn spring-boot:run -pl auth-service -am"
timeout /t 3 /nobreak >nul
start "Content Service" cmd /k "mvn spring-boot:run -pl content-service -am"
timeout /t 3 /nobreak >nul
echo [✓] 后端服务启动中...
echo    - Gateway: http://localhost:8080
echo    - Auth Service: http://localhost:8081
echo    - Content Service: http://localhost:8082
echo.

:: 启动前端服务
echo [4/4] 启动前端服务...
cd /d "%~dp0frontend"
start "Frontend" cmd /k "npm run dev"
echo [✓] 前端服务启动中...
echo    - Frontend: http://localhost:5173
echo.

echo ========================================
echo   所有服务已启动！
echo ========================================
echo.
echo 提示：
echo   - 关闭任意服务窗口即可停止对应服务
echo   - 按 Ctrl+C 可停止当前窗口的服务
echo   - 建议等待 30-60 秒让所有服务完全启动
echo.
pause
