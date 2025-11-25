# Blog Codex - PowerShell 一键启动脚本
# 使用方法: .\start-all.ps1

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Blog Codex - 一键启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 获取脚本所在目录
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path

# 检查 Java 环境
Write-Host "[1/4] 检查 Java 环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "[✓] Java 环境检查通过: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "[✗] 未找到 Java 环境，请先安装 JDK 11 或更高版本" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 检查 Node.js 环境
Write-Host "[2/4] 检查 Node.js 环境..." -ForegroundColor Yellow
try {
    $nodeVersion = node -v
    Write-Host "[✓] Node.js 环境检查通过: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "[✗] 未找到 Node.js 环境，请先安装 Node.js" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 启动后端服务
Write-Host "[3/4] 启动后端服务..." -ForegroundColor Yellow
$backendPath = Join-Path $scriptPath "backend"

# 启动 Gateway
Write-Host "  启动 Gateway..." -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; mvn spring-boot:run -pl gateway -am" -WindowStyle Normal
Start-Sleep -Seconds 5

# 启动 Auth Service
Write-Host "  启动 Auth Service..." -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; mvn spring-boot:run -pl auth-service -am" -WindowStyle Normal
Start-Sleep -Seconds 3

# 启动 Content Service
Write-Host "  启动 Content Service..." -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; mvn spring-boot:run -pl content-service -am" -WindowStyle Normal
Start-Sleep -Seconds 3

Write-Host "[✓] 后端服务启动中..." -ForegroundColor Green
Write-Host "   - Gateway: http://localhost:8080" -ForegroundColor Cyan
Write-Host "   - Auth Service: http://localhost:8081" -ForegroundColor Cyan
Write-Host "   - Content Service: http://localhost:8082" -ForegroundColor Cyan
Write-Host ""

# 启动前端服务
Write-Host "[4/4] 启动前端服务..." -ForegroundColor Yellow
$frontendPath = Join-Path $scriptPath "frontend"
Write-Host "  启动 Frontend..." -ForegroundColor Gray
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; npm run dev" -WindowStyle Normal

Write-Host "[✓] 前端服务启动中..." -ForegroundColor Green
Write-Host "   - Frontend: http://localhost:5173" -ForegroundColor Cyan
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  所有服务已启动！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示：" -ForegroundColor Yellow
Write-Host "  - 关闭任意 PowerShell 窗口即可停止对应服务" -ForegroundColor Gray
Write-Host "  - 按 Ctrl+C 可停止当前窗口的服务" -ForegroundColor Gray
Write-Host "  - 建议等待 30-60 秒让所有服务完全启动" -ForegroundColor Gray
Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
