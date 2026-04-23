package com.senyehui.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    DISABLED(0, "停用"),
    NORMAL(1, "正常");

    private final int code;
    private final String desc;

    public static StatusEnum fromCode(int code) {
        for (StatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的状态码: " + code);
    }
}
