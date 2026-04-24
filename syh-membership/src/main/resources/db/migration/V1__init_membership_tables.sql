-- 会员主表
CREATE TABLE `membership` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `membership_type` VARCHAR(20) NOT NULL COMMENT '会员类型：C_END-C端个人会员, B_END-B端企业会员',
    `level` VARCHAR(20) NOT NULL COMMENT '会员等级：FREE-免费, BRONZE-铜牌, SILVER-银牌, GOLD-金牌, PLATINUM-白金',
    `status` VARCHAR(20) NOT NULL COMMENT '会员状态：ACTIVE-生效中, EXPIRED-已过期, SUSPENDED-已暂停, CANCELLED-已取消',
    `start_time` DATETIME NOT NULL COMMENT '生效时间',
    `end_time` DATETIME COMMENT '过期时间',
    `points` INT DEFAULT 0 COMMENT '会员积分',
    `auto_renew` TINYINT(1) DEFAULT 0 COMMENT '是否自动续费：0-否, 1-是',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_user` (`tenant_id`, `user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员主表';

-- 会员配置表
CREATE TABLE `membership_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `membership_type` VARCHAR(20) NOT NULL COMMENT '会员类型',
    `level` VARCHAR(20) NOT NULL COMMENT '会员等级',
    `name` VARCHAR(100) NOT NULL COMMENT '会员名称',
    `description` TEXT COMMENT '会员描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '会员价格',
    `duration_days` INT NOT NULL COMMENT '有效期天数',
    `benefits` JSON COMMENT '会员权益配置',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用, 1-启用',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_type_level` (`tenant_id`, `membership_type`, `level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员配置表';

-- 会员权益表
CREATE TABLE `membership_benefit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `membership_id` BIGINT NOT NULL COMMENT '会员ID',
    `benefit_type` VARCHAR(50) NOT NULL COMMENT '权益类型',
    `benefit_code` VARCHAR(50) NOT NULL COMMENT '权益代码',
    `benefit_name` VARCHAR(100) NOT NULL COMMENT '权益名称',
    `total_quota` INT COMMENT '总配额',
    `used_quota` INT DEFAULT 0 COMMENT '已使用配额',
    `valid_from` DATETIME NOT NULL COMMENT '生效时间',
    `valid_to` DATETIME NOT NULL COMMENT '失效时间',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_membership` (`membership_id`),
    KEY `idx_benefit_code` (`benefit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员权益表';

-- 会员订单表
CREATE TABLE `membership_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `membership_id` BIGINT COMMENT '会员ID',
    `config_id` BIGINT NOT NULL COMMENT '会员配置ID',
    `membership_type` VARCHAR(20) NOT NULL COMMENT '会员类型',
    `level` VARCHAR(20) NOT NULL COMMENT '会员等级',
    `order_type` VARCHAR(20) NOT NULL COMMENT '订单类型：NEW-新购, RENEW-续费, UPGRADE-升级',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `payment_method` VARCHAR(20) COMMENT '支付方式',
    `payment_time` DATETIME COMMENT '支付时间',
    `status` VARCHAR(20) NOT NULL COMMENT '订单状态：PENDING-待支付, PAID-已支付, CANCELLED-已取消, REFUNDED-已退款',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员订单表';

-- 企业会员表
CREATE TABLE `enterprise_membership` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
    `package_name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `package_level` VARCHAR(20) NOT NULL COMMENT '套餐等级',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `start_time` DATETIME NOT NULL COMMENT '生效时间',
    `end_time` DATETIME COMMENT '过期时间',
    `event_quota` INT COMMENT '赛事配额',
    `used_event_quota` INT DEFAULT 0 COMMENT '已使用赛事配额',
    `participant_quota` INT COMMENT '参赛者配额',
    `used_participant_quota` INT DEFAULT 0 COMMENT '已使用参赛者配额',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_enterprise` (`enterprise_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业会员表';

-- 会员积分表
CREATE TABLE `member_points` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_points` DECIMAL(10,2) DEFAULT 0 COMMENT '累计积分',
    `available_points` DECIMAL(10,2) DEFAULT 0 COMMENT '可用积分',
    `frozen_points` DECIMAL(10,2) DEFAULT 0 COMMENT '冻结积分',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_user` (`tenant_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分表';

-- 积分交易表
CREATE TABLE `point_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型：EARN-获得, CONSUME-消费, EXPIRE-过期, REFUND-退还',
    `points` DECIMAL(10,2) NOT NULL COMMENT '积分数量',
    `description` VARCHAR(200) COMMENT '交易描述',
    `related_type` VARCHAR(50) COMMENT '关联类型',
    `related_id` BIGINT COMMENT '关联ID',
    `expire_time` DATETIME COMMENT '过期时间',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_type` (`transaction_type`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分交易表';
