-- 插入C端会员配置数据
INSERT INTO `membership_config` (`tenant_id`, `membership_type`, `level`, `name`, `description`, `price`, `duration_days`, `benefits`, `sort_order`, `enabled`) VALUES
(1, 'C_END', 'FREE', '免费会员', '基础会员权益', 0.00, 365, '{"registration_discount": 0, "free_registration_count": 0}', 1, 1),
(1, 'C_END', 'BRONZE', '铜牌会员', '享受95折报名优惠', 99.00, 365, '{"registration_discount": 0.95, "free_registration_count": 1}', 2, 1),
(1, 'C_END', 'SILVER', '银牌会员', '享受9折报名优惠', 199.00, 365, '{"registration_discount": 0.90, "free_registration_count": 2}', 3, 1),
(1, 'C_END', 'GOLD', '金牌会员', '享受85折报名优惠', 399.00, 365, '{"registration_discount": 0.85, "free_registration_count": 3}', 4, 1),
(1, 'C_END', 'PLATINUM', '白金会员', '享受8折报名优惠', 699.00, 365, '{"registration_discount": 0.80, "free_registration_count": 5}', 5, 1);

-- 插入B端企业会员配置数据
INSERT INTO `membership_config` (`tenant_id`, `membership_type`, `level`, `name`, `description`, `price`, `duration_days`, `benefits`, `sort_order`, `enabled`) VALUES
(1, 'B_END', 'BASIC', '基础版', '适合小型赛事主办方', 1999.00, 365, '{"event_quota": 10, "participant_quota": 1000, "storage_quota_gb": 50}', 1, 1),
(1, 'B_END', 'PROFESSIONAL', '专业版', '适合中型赛事主办方', 4999.00, 365, '{"event_quota": 50, "participant_quota": 5000, "storage_quota_gb": 200}', 2, 1),
(1, 'B_END', 'ENTERPRISE', '旗舰版', '适合大型赛事主办方', 9999.00, 365, '{"event_quota": 200, "participant_quota": 20000, "storage_quota_gb": 1000}', 3, 1);
