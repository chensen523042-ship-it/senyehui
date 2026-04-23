package com.senyehui.security.dto;

import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息 DTO
 */
@Data
public class UserInfoResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Long tenantId;
    private String tenantName;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限标识列表 */
    private List<String> permissions;
}
