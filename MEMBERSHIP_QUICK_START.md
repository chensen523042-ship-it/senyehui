# 会员系统快速启动指南

## 问题修复

### 修复内容
1. ✅ 添加会员分页查询接口 `/api/membership/page`
2. ✅ 在 `syh-admin` 模块中添加会员模块依赖
3. ✅ 在 `SyhAdminApplication` 中添加会员Mapper扫描
4. ✅ 重新构建项目成功

## 启动步骤

### 1. 数据库初始化

首先确保MySQL数据库已启动，然后执行以下SQL脚本：

```bash
# 创建会员表结构
mysql -u root -p senyehui < syh-membership/src/main/resources/db/migration/V1__init_membership_tables.sql

# 初始化会员配置数据
mysql -u root -p senyehui < syh-membership/src/main/resources/db/migration/V2__init_membership_data.sql
```

或者直接在MySQL客户端中执行这两个SQL文件。

### 2. 启动后端服务

```bash
# 方式1: 使用Maven启动
cd d:/senyehui
mvn spring-boot:run -pl syh-admin

# 方式2: 使用jar包启动
cd d:/senyehui
mvn clean package -DskipTests
java -jar syh-admin/target/syh-admin-1.0.0-SNAPSHOT.jar
```

启动成功后，控制台会显示：
```
====================================
  森野汇 Admin API 启动成功！
  文档地址: http://localhost:8080/doc.html
====================================
```

### 3. 启动前端

```bash
cd syh-admin-ui
npm install  # 首次运行需要安装依赖
npm run dev
```

前端启动后访问: http://localhost:5173

### 4. 验证会员功能

#### 测试API接口

1. **查询会员列表**
```bash
curl http://localhost:8080/api/membership/page?pageNum=1&pageSize=10
```

2. **查询会员配置**
```bash
curl http://localhost:8080/api/membership/config/list
```

3. **查看API文档**
访问: http://localhost:8080/doc.html

#### 测试前端界面

1. 登录管理后台: http://localhost:5173
2. 在侧边栏找到"会员管理"菜单
3. 点击进入会员列表页面
4. 点击"会员配置"查看会员等级配置

## 常见问题

### 问题1: 404错误 - 请求的资源不存在

**原因**: 会员模块未被正确加载到admin应用中

**解决方案**:
- ✅ 已在 `syh-admin/pom.xml` 中添加会员模块依赖
- ✅ 已在 `SyhAdminApplication` 中添加会员Mapper扫描
- 重新构建并启动服务

### 问题2: 数据库连接失败

**检查**:
- MySQL服务是否启动
- 数据库配置是否正确 (application.yml)
- 数据库用户名密码是否正确

### 问题3: 前端无法连接后端

**检查**:
- 后端服务是否启动 (端口8080)
- 前端代理配置是否正确 (vite.config.js)
- 浏览器控制台是否有CORS错误

## 会员API接口列表

### 会员管理
- `GET /api/membership/page` - 分页查询会员列表
- `GET /api/membership/user/{userId}` - 查询用户会员信息
- `POST /api/membership/purchase` - 购买会员
- `POST /api/membership/renew/{membershipId}` - 续费会员
- `POST /api/membership/upgrade/{membershipId}` - 升级会员
- `POST /api/membership/cancel/{membershipId}` - 取消会员
- `GET /api/membership/check/{userId}` - 检查会员有效性

### 会员配置
- `GET /api/membership/config/list` - 查询配置列表
- `GET /api/membership/config/{id}` - 查询配置详情
- `POST /api/membership/config` - 创建配置
- `PUT /api/membership/config/{id}` - 更新配置
- `DELETE /api/membership/config/{id}` - 删除配置

### 会员权益
- `GET /api/membership/benefit/list/{membershipId}` - 查询权益列表
- `POST /api/membership/benefit/allocate` - 分配权益
- `POST /api/membership/benefit/consume/{benefitId}` - 消费权益
- `GET /api/membership/benefit/check` - 检查权益可用性

## 测试数据

系统已初始化以下会员配置：

### C端个人会员
| 等级 | 名称 | 价格 | 折扣 | 免费报名 |
|------|------|------|------|----------|
| FREE | 免费会员 | ¥0 | 无 | 0次 |
| BRONZE | 铜牌会员 | ¥99/年 | 95折 | 1次 |
| SILVER | 银牌会员 | ¥199/年 | 90折 | 2次 |
| GOLD | 金牌会员 | ¥399/年 | 85折 | 3次 |
| PLATINUM | 白金会员 | ¥699/年 | 80折 | 5次 |

### B端企业会员
| 等级 | 名称 | 价格 | 赛事配额 | 参赛者配额 |
|------|------|------|----------|------------|
| BASIC | 基础版 | ¥1999/年 | 10场 | 1000人 |
| PROFESSIONAL | 专业版 | ¥4999/年 | 50场 | 5000人 |
| ENTERPRISE | 旗舰版 | ¥9999/年 | 200场 | 20000人 |

## 下一步

1. 测试会员购买流程
2. 测试报名时的会员折扣
3. 测试会员权益核销
4. 开发会员数据分析功能
5. 完善单元测试

## 技术支持

如遇到问题，请检查：
1. 后端日志: 查看控制台输出
2. 前端日志: 浏览器开发者工具 Console
3. API文档: http://localhost:8080/doc.html
4. 数据库: 检查表是否创建成功

## 更新日志

### 2026-04-24
- ✅ 修复会员分页查询接口404错误
- ✅ 添加会员模块到admin应用
- ✅ 更新Mapper扫描配置
- ✅ 重新构建项目成功
