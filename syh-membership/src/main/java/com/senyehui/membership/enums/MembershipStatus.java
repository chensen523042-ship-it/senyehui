package com.senyehui.membership.enums;

public enum MembershipStatus {
    ACTIVE("ACTIVE", "生效中"),
    EXPIRED("EXPIRED", "已过期"),
    OVERDUE("OVERDUE", "欠费中"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    MembershipStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
