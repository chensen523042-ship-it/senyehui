package com.senyehui.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 认证相关 4xx
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    TOKEN_EXPIRED(401, "Token 已过期"),
    TOKEN_INVALID(401, "Token 无效"),

    // 参数相关
    PARAM_ERROR(400, "参数错误"),
    PARAM_MISSING(400, "缺少必要参数"),

    // 业务相关
    DATA_NOT_FOUND(404, "数据不存在"),
    DATA_ALREADY_EXISTS(409, "数据已存在"),
    TENANT_DISABLED(403, "租户已停用"),
    TENANT_EXPIRED(403, "租户已过期"),

    // 报名相关
    QUOTA_FULL(410, "报名名额已满"),
    REG_TIME_NOT_OPEN(411, "报名时间未开始"),
    REG_TIME_CLOSED(412, "报名已截止"),
    ALREADY_REGISTERED(413, "已报名，请勿重复提交"),

    // 系统相关
    SYSTEM_ERROR(500, "系统内部错误"),
    SERVICE_BUSY(503, "系统繁忙，请稍后重试");

    private final int code;
    private final String msg;
}
