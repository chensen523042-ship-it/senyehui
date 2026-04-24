# 会员服务模块 (syh-membership)

## 概述

会员服务模块为户外赛事SaaS系统提供完整的会员体系，支持C端个人会员和B端企业会员。

## 功能特性

### C端个人会员
- **会员等级**: 免费/铜牌/银牌/金牌/白金
- **核心功能**:
  - 会员购买和开通
  - 会员信息查询
  - 会员续费和升级
  - 会员取消
  - 报名折扣（根据等级享受不同折扣）
  - 免费报名权益（根据等级获得不同次数）
  - 积分累积和消费

### B端企业会员
- **套餐等级**: 基础版/专业版/旗舰版
- **核心功能**:
  - 企业套餐购买
  - 资源配额管理（赛事数/参赛人数/存储空间）
  - 配额使用追踪
  - 套餐升级

### 会员权益管理
- 权益自动分配
- 权益实时验证
- 权益核销
- 权益使用记录追踪

## 技术架构

- **框架**: Spring Boot 3.2.5
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **分布式锁**: Redisson

## 数据库表结构

### 核心表
- `membership`: 会员主表
- `membership_config`: 会员配置表
- `membership_benefit`: 会员权益表
- `membership_order`: 会员订单表
- `enterprise_membership`: 企业会员表
- `member_points`: 会员积分表
- `point_transaction`: 积分交易表

## API接口

### 会员管理
```
GET    /api/membership/user/{userId}           # 查询用户会员信息
POST   /api/membership/purchase                # 购买会员
POST   /api/membership/renew/{membershipId}    # 续费会员
POST   /api/membership/upgrade/{membershipId}  # 升级会员
POST   /api/membership/cancel/{membershipId}   # 取消会员
GET    /api/membership/check/{userId}          # 检查会员有效性
```

### 会员配置
```
GET    /api/membership/config/list             # 查询会员配置列表
GET    /api/membership/config/{id}             # 查询会员配置详情
POST   /api/membership/config                  # 创建会员配置
PUT    /api/membership/config/{id}             # 更新会员配置
DELETE /api/membership/config/{id}             # 删除会员配置
```

### 会员权益
```
GET    /api/membership/benefit/list/{membershipId}  # 查询会员权益列表
POST   /api/membership/benefit/allocate             # 分配权益
POST   /api/membership/benefit/consume/{benefitId} # 消费权益
GET    /api/membership/benefit/check                # 检查权益可用性
```

## 集成说明

### 在报名流程中集成会员权益

1. 添加依赖到报名模块的pom.xml:
```xml
<dependency>
    <groupId>com.senyehui</groupId>
    <artifactId>syh-membership</artifactId>
</dependency>
```

2. 使用MembershipIntegration进行权益验证:
```java
@Autowired
private MembershipIntegration membershipIntegration;

// 应用会员折扣
BigDecimal discountedAmount = membershipIntegration.applyMembershipDiscount(userId, originalAmount);

// 检查免费报名权益
boolean hasFreeRegistration = membershipIntegration.checkFreeRegistrationBenefit(userId);

// 消费免费报名权益
membershipIntegration.consumeFreeRegistrationBenefit(userId);
```

## 部署步骤

1. 执行数据库迁移脚本:
```bash
# 创建表结构
mysql -u root -p senyehui < syh-membership/src/main/resources/db/migration/V1__init_membership_tables.sql

# 初始化配置数据
mysql -u root -p senyehui < syh-membership/src/main/resources/db/migration/V2__init_membership_data.sql
```

2. 配置application.yml:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/senyehui
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

3. 启动服务:
```bash
mvn clean install
java -jar syh-membership/target/syh-membership-1.0.0-SNAPSHOT.jar
```

## 配置说明

### 会员等级折扣配置
在MembershipIntegration中配置不同等级的折扣率:
- 银牌会员: 95折
- 金牌会员: 90折
- 钻石会员: 85折

### 会员权益代码
- `REGISTRATION_DISCOUNT`: 报名折扣权益
- `FREE_REGISTRATION`: 免费报名权益
- `PRIORITY_SUPPORT`: 优先客服支持
- `EXCLUSIVE_EVENT`: 专属赛事参与权

## 后续开发计划

- [ ] 实现会员积分累积和消费功能
- [ ] 实现会员自动续费功能
- [ ] 实现会员续费提醒
- [ ] 实现会员数据分析和报表
- [ ] 开发会员管理后台界面
- [ ] 开发C端会员中心页面
- [ ] 实现B端企业会员完整功能
