package com.senyehui.membership.enums;

public enum MembershipType {
    C_USER("C_USER", "C端用户会员"),
    B_ORGANIZER("B_ORGANIZER", "B端主办方会员");

    private final String code;
    private final String description;

    MembershipType(String code, String description) {
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
