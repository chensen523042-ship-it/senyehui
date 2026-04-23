-- ============================================================
-- 森野汇 SaaS 系统 — 数据库初始化脚本
-- 数据库: db_syh_saas
-- 字符集: utf8mb4
-- ============================================================

-- 创建数据库（如不存在）
CREATE DATABASE IF NOT EXISTS `db_syh_saas` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `db_syh_saas`;

-- ============================================================
-- 1. 租户套餐表
-- ============================================================
DROP TABLE IF EXISTS `sys_tenant_package`;
CREATE TABLE `sys_tenant_package` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `package_name`    VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `max_users`       INT DEFAULT 0 COMMENT '最大用户数（0=不限）',
    `max_events`      INT DEFAULT 0 COMMENT '最大赛事数（0=不限）',
    `max_storage`     BIGINT DEFAULT 0 COMMENT '最大存储空间（字节，0=不限）',
    `features`        JSON COMMENT '功能列表 JSON',
    `price`           DECIMAL(10,2) DEFAULT 0.00 COMMENT '月费',
    `status`          TINYINT DEFAULT 1 COMMENT '0-停用 1-正常',
    `remark`          VARCHAR(500) COMMENT '备注',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户套餐表';

-- ============================================================
-- 2. 租户表
-- ============================================================
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_code`     VARCHAR(50) UNIQUE NOT NULL COMMENT '租户编码',
    `tenant_name`     VARCHAR(100) NOT NULL COMMENT '组织者名称',
    `domain`          VARCHAR(200) COMMENT '自定义域名',
    `logo_url`        VARCHAR(500) COMMENT 'Logo',
    `contact_name`    VARCHAR(50) COMMENT '联系人',
    `contact_phone`   VARCHAR(20) COMMENT '联系电话',
    `contact_email`   VARCHAR(100) COMMENT '联系邮箱',
    `package_id`      BIGINT COMMENT '套餐ID',
    `status`          TINYINT DEFAULT 1 COMMENT '0-停用 1-正常 2-试用',
    `expire_time`     DATETIME COMMENT '套餐到期时间',
    `remark`          VARCHAR(500) COMMENT '备注',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ============================================================
-- 3. 系统用户表
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `username`        VARCHAR(50) NOT NULL COMMENT '用户名',
    `password`        VARCHAR(200) NOT NULL COMMENT '密码（BCrypt）',
    `nickname`        VARCHAR(50) COMMENT '昵称',
    `phone`           VARCHAR(20) COMMENT '手机号',
    `email`           VARCHAR(100) COMMENT '邮箱',
    `avatar`          VARCHAR(500) COMMENT '头像',
    `status`          TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `last_login_time` DATETIME COMMENT '最近登录时间',
    `last_login_ip`   VARCHAR(50) COMMENT '最近登录IP',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`       VARCHAR(50) COMMENT '创建人',
    `update_by`       VARCHAR(50) COMMENT '更新人',
    `deleted`         TINYINT DEFAULT 0,
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================================
-- 4. 角色表
-- ============================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `role_code`       VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name`       VARCHAR(100) NOT NULL COMMENT '角色名称',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `status`          TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `remark`          VARCHAR(500) COMMENT '备注',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`       VARCHAR(50),
    `update_by`       VARCHAR(50),
    `deleted`         TINYINT DEFAULT 0,
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 5. 权限/菜单表（全局，不按租户隔离）
-- ============================================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `parent_id`       BIGINT DEFAULT 0 COMMENT '父级ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '权限名称',
    `perms`           VARCHAR(200) COMMENT '权限标识（如 system:user:list）',
    `type`            TINYINT NOT NULL COMMENT '1-目录 2-菜单 3-按钮',
    `path`            VARCHAR(300) COMMENT '路由路径',
    `component`       VARCHAR(300) COMMENT '组件路径',
    `icon`            VARCHAR(100) COMMENT '图标',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `visible`         TINYINT DEFAULT 1 COMMENT '0-隐藏 1-显示',
    `status`          TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限/菜单表';

-- ============================================================
-- 6. 用户-角色关联表
-- ============================================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT NOT NULL COMMENT '用户ID',
    `role_id`         BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================================
-- 7. 角色-权限关联表
-- ============================================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `role_id`         BIGINT NOT NULL COMMENT '角色ID',
    `permission_id`   BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 8. 赛事分类表
-- ============================================================
DROP TABLE IF EXISTS `event_category`;
CREATE TABLE `event_category` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '分类名称',
    `icon`            VARCHAR(200) COMMENT '图标',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `status`          TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`       VARCHAR(50) COMMENT '创建人',
    `update_by`       VARCHAR(50) COMMENT '更新人',
    `deleted`         TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事分类表';

-- ============================================================
-- 9. 赛事活动表
-- ============================================================
DROP TABLE IF EXISTS `event_info`;
CREATE TABLE `event_info` (
    `id`                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`             BIGINT NOT NULL COMMENT '租户ID',
    `title`                 VARCHAR(200) NOT NULL COMMENT '赛事标题',
    `category_id`           BIGINT COMMENT '分类ID',
    `event_type`            TINYINT COMMENT '1-赛事 2-活动 3-培训 4-露营',
    `cover_image`           VARCHAR(500) COMMENT '封面图',
    `description`           TEXT COMMENT '详情描述（富文本）',
    `start_time`            DATETIME COMMENT '活动开始时间',
    `end_time`              DATETIME COMMENT '活动结束时间',
    `reg_start_time`        DATETIME COMMENT '报名开始时间',
    `reg_end_time`          DATETIME COMMENT '报名截止时间',
    `location`              VARCHAR(300) COMMENT '活动地点',
    `address`               VARCHAR(500) COMMENT '详细地址',
    `longitude`             DECIMAL(10,7) COMMENT '经度',
    `latitude`              DECIMAL(10,7) COMMENT '纬度',
    `max_participants`      INT DEFAULT 0 COMMENT '最大参与人数（0=不限）',
    `current_participants`  INT DEFAULT 0 COMMENT '当前报名人数',
    `price`                 DECIMAL(10,2) DEFAULT 0.00 COMMENT '报名费',
    `status`                TINYINT DEFAULT 0 COMMENT '0-草稿 1-待审核 2-已发布 3-报名中 4-进行中 5-已结束 6-已取消',
    `sort_order`            INT DEFAULT 0 COMMENT '排序权重',
    `view_count`            INT DEFAULT 0 COMMENT '浏览量',
    `form_template_id`      BIGINT COMMENT '关联报名表单模板',
    `create_time`           DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`             VARCHAR(50),
    `update_by`             VARCHAR(50),
    `deleted`               TINYINT DEFAULT 0,
    INDEX `idx_tenant_status` (`tenant_id`, `status`),
    INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事活动表';

-- ============================================================
-- 10. 赛事组别表
-- ============================================================
DROP TABLE IF EXISTS `event_group`;
CREATE TABLE `event_group` (
    `id`                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`             BIGINT NOT NULL COMMENT '租户ID',
    `event_id`              BIGINT NOT NULL COMMENT '赛事ID',
    `group_name`            VARCHAR(100) NOT NULL COMMENT '组别名称',
    `max_participants`      INT DEFAULT 0 COMMENT '最大人数（0=不限）',
    `current_participants`  INT DEFAULT 0 COMMENT '当前报名人数',
    `price`                 DECIMAL(10,2) DEFAULT 0.00 COMMENT '报名费',
    `sort_order`            INT DEFAULT 0,
    `status`                TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `create_time`           DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`             VARCHAR(50) COMMENT '创建人',
    `update_by`             VARCHAR(50) COMMENT '更新人',
    `deleted`               TINYINT DEFAULT 0,
    INDEX `idx_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事组别表';

-- ============================================================
-- 11. 报名记录表
-- ============================================================
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration` (
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`         BIGINT NOT NULL COMMENT '租户ID',
    `event_id`          BIGINT NOT NULL COMMENT '赛事ID',
    `group_id`          BIGINT COMMENT '组别ID',
    `user_id`           BIGINT COMMENT '报名用户ID',
    `reg_no`            VARCHAR(50) COMMENT '报名编号',
    `name`              VARCHAR(50) COMMENT '参赛者姓名',
    `phone`             VARCHAR(20) COMMENT '手机号',
    `id_card`           VARCHAR(200) COMMENT '身份证号（加密存储）',
    `form_data_id`      BIGINT COMMENT '关联表单数据',
    `amount`            DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
    `pay_status`        TINYINT DEFAULT 0 COMMENT '0-未支付 1-已支付 2-已退款',
    `pay_time`          DATETIME COMMENT '支付时间',
    `pay_order_no`      VARCHAR(64) COMMENT '支付订单号',
    `status`            TINYINT DEFAULT 0 COMMENT '0-待审核 1-已通过 2-已拒绝 3-已取消',
    `check_in_status`   TINYINT DEFAULT 0 COMMENT '0-未签到 1-已签到',
    `check_in_time`     DATETIME COMMENT '签到时间',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`         VARCHAR(50),
    `update_by`         VARCHAR(50),
    `deleted`           TINYINT DEFAULT 0,
    UNIQUE KEY `uk_event_user` (`event_id`, `user_id`),
    INDEX `idx_tenant_event` (`tenant_id`, `event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名记录表';

-- ============================================================
-- 12. 表单模板表
-- ============================================================
DROP TABLE IF EXISTS `form_template`;
CREATE TABLE `form_template` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '模板名称',
    `description`     VARCHAR(500) COMMENT '描述',
    `status`          TINYINT DEFAULT 1 COMMENT '0-禁用 1-启用',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`       VARCHAR(50),
    `update_by`       VARCHAR(50),
    `deleted`         TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单模板表';

-- ============================================================
-- 13. 表单字段表
-- ============================================================
DROP TABLE IF EXISTS `form_field`;
CREATE TABLE `form_field` (
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `template_id`       BIGINT NOT NULL COMMENT '模板ID',
    `field_key`         VARCHAR(50) NOT NULL COMMENT '字段标识',
    `field_label`       VARCHAR(100) NOT NULL COMMENT '字段标签',
    `field_type`        VARCHAR(20) NOT NULL COMMENT '字段类型: text/number/select/radio/checkbox/date/file/phone/idcard',
    `placeholder`       VARCHAR(200) COMMENT '占位提示',
    `default_value`     VARCHAR(500) COMMENT '默认值',
    `options`           JSON COMMENT '选项列表 [{label,value}]',
    `validation_rules`  JSON COMMENT '校验规则 {required,min,max,pattern,message}',
    `sort_order`        INT DEFAULT 0 COMMENT '排序',
    `required`          TINYINT DEFAULT 0 COMMENT '是否必填',
    `visible`           TINYINT DEFAULT 1 COMMENT '是否可见',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_template` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单字段表';

-- ============================================================
-- 14. 表单数据表
-- ============================================================
DROP TABLE IF EXISTS `form_data`;
CREATE TABLE `form_data` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tenant_id`       BIGINT NOT NULL COMMENT '租户ID',
    `template_id`     BIGINT NOT NULL COMMENT '模板ID',
    `event_id`        BIGINT COMMENT '赛事ID',
    `user_id`         BIGINT COMMENT '提交用户ID',
    `form_values`     JSON NOT NULL COMMENT '表单数据 {fieldKey: value}',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_template_event` (`template_id`, `event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单数据表';


-- ============================================================
-- ==================== 初始化数据 ============================
-- ============================================================

-- ---------- 租户套餐 ----------
INSERT INTO `sys_tenant_package` (`id`, `package_name`, `max_users`, `max_events`, `price`, `status`, `remark`) VALUES
(1, '免费套餐', 5, 10, 0.00, 1, '免费体验版，适合个人组织者'),
(2, '基础套餐', 20, 50, 99.00, 1, '适合小型赛事组织者'),
(3, '专业套餐', 100, 500, 399.00, 1, '适合中型赛事公司'),
(4, '企业套餐', 0, 0, 999.00, 1, '不限用户/赛事，适合大型赛事平台');

-- ---------- 默认平台租户 ----------
INSERT INTO `sys_tenant` (`id`, `tenant_code`, `tenant_name`, `status`, `package_id`, `remark`) VALUES
(1, 'platform', '森野汇平台', 1, 4, '平台超级管理租户');

-- ---------- 超级管理员用户 ----------
-- 密码: admin123 (BCrypt 加密)
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `status`) VALUES
(1, 1, 'admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PbRqLnG0C.qd2G4.Kg0S6', '超级管理员', 1);

-- ---------- 角色 ----------
INSERT INTO `sys_role` (`id`, `tenant_id`, `role_code`, `role_name`, `sort_order`, `status`) VALUES
(1, 1, 'SUPER_ADMIN', '超级管理员', 0, 1),
(2, 1, 'TENANT_ADMIN', '租户管理员', 1, 1),
(3, 1, 'OPERATOR', '运营人员', 2, 1);

-- ---------- 用户-角色关联 ----------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- ---------- 权限/菜单 ----------
-- 一级目录
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `type`, `path`, `icon`, `sort_order`, `status`) VALUES
(100, 0, '系统管理', 1, '/system', 'Setting', 1, 1),
(200, 0, '租户管理', 1, '/tenant', 'OfficeBuilding', 2, 1),
(300, 0, '赛事管理', 1, '/event', 'Trophy', 3, 1),
(400, 0, '报名管理', 1, '/registration', 'DocumentChecked', 4, 1),
(500, 0, '表单管理', 1, '/form', 'Edit', 5, 1),
(600, 0, '数据统计', 1, '/statistics', 'DataLine', 6, 1);

-- 系统管理 - 二级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
(101, 100, '用户管理', 'system:user:list', 2, 'user', 'system/UserManage', 'User', 1, 1),
(102, 100, '角色管理', 'system:role:list', 2, 'role', 'system/RoleManage', 'UserFilled', 2, 1),
(103, 100, '菜单管理', 'system:menu:list', 2, 'menu', 'system/MenuManage', 'Menu', 3, 1),
(104, 100, '系统设置', 'system:setting:view', 2, 'setting', 'system/Setting', 'Tools', 4, 1);

-- 租户管理 - 二级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
(201, 200, '租户列表', 'tenant:list', 2, 'list', 'tenant/TenantList', 'List', 1, 1),
(202, 200, '套餐管理', 'tenant:package:list', 2, 'package', 'tenant/PackageManage', 'GoodsFilled', 2, 1);

-- 赛事管理 - 二级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
(301, 300, '赛事列表', 'event:list', 2, 'list', 'event/EventList', 'List', 1, 1),
(302, 300, '赛事分类', 'event:category:list', 2, 'category', 'event/CategoryManage', 'Collection', 2, 1);

-- 报名管理 - 二级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
(401, 400, '报名列表', 'registration:list', 2, 'list', 'registration/RegistrationList', 'List', 1, 1),
(402, 400, '签到管理', 'registration:checkin', 2, 'checkin', 'registration/CheckIn', 'CircleCheck', 2, 1);

-- 表单管理 - 二级菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
(501, 500, '表单模板', 'form:template:list', 2, 'template', 'form/FormDesigner', 'EditPen', 1, 1);

-- 按钮权限（用户管理模块示例）
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `perms`, `type`, `sort_order`, `status`) VALUES
(1011, 101, '新增用户', 'system:user:add', 3, 1, 1),
(1012, 101, '编辑用户', 'system:user:edit', 3, 2, 1),
(1013, 101, '删除用户', 'system:user:delete', 3, 3, 1),
(1014, 101, '重置密码', 'system:user:resetPwd', 3, 4, 1);

-- ---------- 超管角色 → 所有权限 ----------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `sys_permission`;

-- ---------- 租户管理员 → 赛事/报名/表单/数据统计权限 ----------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 2, `id` FROM `sys_permission` WHERE `id` >= 300;
