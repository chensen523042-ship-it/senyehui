package com.senyehui.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 赛事活动类型枚举
 */
@Getter
@AllArgsConstructor
public enum EventTypeEnum {

    COMPETITION(1, "赛事"),
    ACTIVITY(2, "活动"),
    TRAINING(3, "培训"),
    CAMPING(4, "露营");

    private final int code;
    private final String desc;

    public static EventTypeEnum fromCode(int code) {
        for (EventTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的赛事类型: " + code);
    }
}
