---
description: 在 PowerShell 中执行 MySQL 命令的正确方式
---

# PowerShell 执行 MySQL 命令

## 关键注意事项
- PowerShell 中 `$` 是变量符号，BCrypt 哈希中的 `$` 会被解析
- PowerShell 不支持 `<` 输入重定向，需用 `cmd /c` 或 `Get-Content | mysql`
- MySQL 的 `-p` 参数在命令行中会产生 Warning，stderr 导致 exit code 1，但实际执行成功

## 方式1: 使用 SQL 文件（推荐）
```powershell
# 创建 SQL 文件（内容不受 PowerShell 变量替换影响）
# 然后用 cmd 执行
cmd /c 'type d:\senyehui\temp.sql | "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas'
```

## 方式2: 使用 -e 参数（简单查询）
```powershell
# 注意：SQL 中不能有 $ 符号，否则需要用 cmd /c
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas -e "SELECT * FROM sys_user"'
```

## 方式3: 使用 Get-Content 管道
```powershell
Get-Content "d:\senyehui\temp.sql" -Raw | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas
```

## MySQL 客户端路径
```
C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
```

## 常用命令
```powershell
# 查看所有表
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas -e "SHOW TABLES"'

# 查看表结构
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas -e "DESC sys_user"'

# 查看数据
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot db_syh_saas -e "SELECT id,username,status FROM sys_user"'
```
