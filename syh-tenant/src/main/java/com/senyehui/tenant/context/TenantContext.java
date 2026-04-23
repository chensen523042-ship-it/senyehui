package com.senyehui.tenant.context;

/**
 * 多租户上下文 - 基于 ThreadLocal 存储当前请求的租户 ID
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private TenantContext() {}

    /**
     * 设置当前租户 ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前租户 ID
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 清除当前租户 ID（请求结束时必须调用，防止内存泄漏）
     */
    public static void clear() {
        TENANT_ID.remove();
    }

    /**
     * 判断当前是否有租户上下文
     */
    public static boolean hasTenant() {
        return TENANT_ID.get() != null;
    }
}
