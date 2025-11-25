@echo off
chcp 65001 >nul
echo ========================================
echo   Blog Codex - 停止所有服务
echo ========================================
echo.

echo 正在停止所有服务...

:: 停止 Java 进程 (后端服务)
echo 停止后端服务...
taskkill /F /FI "WINDOWTITLE eq Gateway*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Auth Service*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Content Service*" >nul 2>&1

:: 停止前端服务 (Node.js)
echo 停止前端服务...
taskkill /F /FI "WINDOWTITLE eq Frontend*" >nul 2>&1

:: 也可以直接杀掉所有相关端口的进程
echo 清理端口占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /F /PID %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /F /PID %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8082') do taskkill /F /PID %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173') do taskkill /F /PID %%a >nul 2>&1

echo.
echo [✓] 所有服务已停止
echo.
pause
