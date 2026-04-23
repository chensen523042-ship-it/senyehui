package com.senyehui.registration.dto;

import lombok.Data;

import java.util.Map;

/**
 * 报名请求 DTO
 */
@Data
public class RegisterRequest {

    /** 赛事 ID */
    private Long eventId;

    /** 组别 ID（可选，如果赛事有组别） */
    private Long groupId;

    /** 参赛者姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 身份证号 */
    private String idCard;

    /** 报名表单数据 (动态字段: {fieldKey: value}) */
    private Map<String, Object> formValues;
}
