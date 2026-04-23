package com.senyehui.common.constant;

/**
 * 系统常量
 */
public final class Constants {

    private Constants() {}

    // ========== 认证相关 ==========

    /** Token 请求头 */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 租户 ID 请求头 */
    public static final String TENANT_HEADER = "X-Tenant-Id";

    // ========== 缓存 Key 前缀 ==========

    /** 缓存 Key 前缀 */
    public static final String CACHE_PREFIX = "syh:";

    /** 用户信息缓存 */
    public static final String CACHE_USER = CACHE_PREFIX + "user:";

    /** Token 黑名单 */
    public static final String CACHE_TOKEN_BLACKLIST = CACHE_PREFIX + "token:blacklist:";

    /** 报名锁 */
    public static final String LOCK_REGISTRATION = CACHE_PREFIX + "lock:reg:";

    /** 名额缓存 */
    public static final String CACHE_QUOTA = CACHE_PREFIX + "quota:";

    // ========== 默认值 ==========

    /** 超级管理员角色编码 */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /** 租户管理员角色编码 */
    public static final String ROLE_TENANT_ADMIN = "TENANT_ADMIN";

    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "123456";

    /** 默认租户 ID (平台级) */
    public static final Long DEFAULT_TENANT_ID = 1L;
}
