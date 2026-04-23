package com.senyehui.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** Access Token */
    private String accessToken;

    /** Refresh Token */
    private String refreshToken;

    /** Access Token 过期时间（毫秒时间戳） */
    private Long expiresIn;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 租户 ID */
    private Long tenantId;

    /** 租户名称 */
    private String tenantName;
}
