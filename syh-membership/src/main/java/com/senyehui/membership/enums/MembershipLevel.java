package com.senyehui.membership.enums;

public enum MembershipLevel {
    NORMAL("NORMAL", "普通会员", 0),
    SILVER("SILVER", "银卡会员", 1),
    GOLD("GOLD", "金卡会员", 2),
    DIAMOND("DIAMOND", "钻石会员", 3);

    private final String code;
    private final String description;
    private final Integer level;

    MembershipLevel(String code, String description, Integer level) {
        this.code = code;
        this.description = description;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLevel() {
        return level;
    }
}
