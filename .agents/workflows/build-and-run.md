---
description: 构建并运行森野汇 SaaS 后端项目
---

# 构建并运行项目

## 前置条件
- MySQL 8.0 运行中 (端口 3306，用户 root/root)
- Redis 运行中 (端口 6379)
- JDK 17+, Maven 3.9+

## 构建步骤

// turbo-all

1. 构建所有模块并安装到本地仓库
```powershell
cd d:\senyehui
mvn clean install -DskipTests -q
```

2. 仅编译（快速验证，不安装）
```powershell
cd d:\senyehui
mvn clean compile -DskipTests
```

## 运行步骤

3. 启动 Admin 后台 API（端口 8080）
```powershell
cd d:\senyehui
mvn spring-boot:run -pl syh-admin -DskipTests
```

4. 启动 C端 API（端口 8081）
```powershell
cd d:\senyehui
mvn spring-boot:run -pl syh-api -DskipTests
```

## 验证启动成功
```powershell
# 检查端口是否监听
Test-NetConnection -ComputerName 127.0.0.1 -Port 8080 -WarningAction SilentlyContinue -InformationLevel Quiet
```

## 注意事项
- 必须先 `mvn install` 再 `spring-boot:run`，因为子模块之间有依赖
- 如果修改了 common/tenant/security 等底层模块，需要重新 `mvn install`
- 如果只修改了 syh-admin 自身代码，可以直接 `spring-boot:run`
