package com.senyehui.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 赛事状态枚举
 */
@Getter
@AllArgsConstructor
public enum EventStatusEnum {

    DRAFT(0, "草稿"),
    PENDING_REVIEW(1, "待审核"),
    PUBLISHED(2, "已发布"),
    REGISTRATION_OPEN(3, "报名中"),
    IN_PROGRESS(4, "进行中"),
    ENDED(5, "已结束"),
    CANCELLED(6, "已取消");

    private final int code;
    private final String desc;

    public static EventStatusEnum fromCode(int code) {
        for (EventStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的赛事状态: " + code);
    }
}
