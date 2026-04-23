---
description: 测试森野汇 SaaS 系统 REST API
---

# API 测试流程

## PowerShell 中调用 REST API 的标准方式

### 1. POST 请求（JSON Body）
```powershell
$body = @{
    username = "admin"
    password = "admin123"
    tenantId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($body)) | ConvertTo-Json -Depth 5
```

### 2. 带 Token 的 GET 请求
```powershell
$token = "你的AccessToken"
$headers = @{ Authorization = "Bearer $token"; "X-Tenant-Id" = "1" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/userinfo" -Method GET -Headers $headers | ConvertTo-Json -Depth 5
```

### 3. 带 Token 的 POST 请求
```powershell
$token = "你的AccessToken"
$headers = @{ Authorization = "Bearer $token"; "X-Tenant-Id" = "1" }
$body = @{ key = "value" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/xxx" -Method POST -ContentType "application/json; charset=utf-8" -Headers $headers -Body ([System.Text.Encoding]::UTF8.GetBytes($body)) | ConvertTo-Json -Depth 5
```

## Phase 2 验证流程

### Step 1: 登录获取 Token
```powershell
$loginBody = @{ username = "admin"; password = "admin123"; tenantId = 1 } | ConvertTo-Json
$loginResult = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($loginBody))
$loginResult | ConvertTo-Json -Depth 5
$token = $loginResult.data.accessToken
```

### Step 2: 获取当前用户信息
```powershell
$headers = @{ Authorization = "Bearer $token"; "X-Tenant-Id" = "1" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/userinfo" -Method GET -Headers $headers | ConvertTo-Json -Depth 5
```

### Step 3: 刷新 Token
```powershell
$refreshBody = @{ refreshToken = $loginResult.data.refreshToken } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/refresh" -Method POST -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($refreshBody)) | ConvertTo-Json -Depth 5
```

### Step 4: 登出
```powershell
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/logout" -Method POST -Headers $headers | ConvertTo-Json -Depth 5
```

## 注意事项
- PowerShell 的 Invoke-RestMethod 默认编码可能导致中文乱码，始终使用 UTF8.GetBytes
- Token 有效期 2 小时，Refresh Token 有效期 7 天
- API 文档地址: http://localhost:8080/doc.html
