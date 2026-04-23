package com.senyehui.wechat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.senyehui.common.constant.Constants;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.security.dto.LoginResponse;
import com.senyehui.security.entity.SysUser;
import com.senyehui.security.jwt.JwtTokenProvider;
import com.senyehui.security.mapper.SysUserMapper;
import com.senyehui.tenant.context.TenantContext;
import com.senyehui.tenant.entity.Tenant;
import com.senyehui.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 微信登录 + 手机号登录服务（模拟模式）
 *
 * 模拟模式说明：
 * - 微信登录：任意 code 均生成模拟 openid，自动注册/登录
 * - 手机号登录：任意验证码均验证通过，自动注册/登录
 * - 对接真实微信时，只需替换 openid 获取逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxAuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;

    /**
     * 微信登录（模拟模式）
     * 
     * @param code 微信授权 code（模拟模式下任意值）
     * @return JWT 登录响应
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse wxLogin(String code) {
        // 模拟获取 openid（真实模式需调用微信 API：code → access_token → openid）
        String openid = "mock_openid_" + code.hashCode();
        log.info("微信登录(模拟): code={}, openid={}", code, openid);

        Long tenantId = resolveTenantId();

        // 用 openid 作为 username 查找或创建用户
        String username = "wx_" + openid;
        SysUser user = sysUserMapper.selectByUsername(username, tenantId);

        if (user == null) {
            // 自动注册
            user = createWxUser(username, "微信用户", null, tenantId);
            log.info("微信用户自动注册: username={}, userId={}", username, user.getId());
        }

        return buildLoginResponse(user, tenantId);
    }

    /**
     * 手机号验证码登录（模拟模式）
     *
     * @param phone   手机号
     * @param smsCode 验证码（模拟模式下任意值）
     * @return JWT 登录响应
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse phoneLogin(String phone, String smsCode) {
        // 模拟验证码校验（真实模式需对接短信平台验证）
        log.info("手机号登录(模拟): phone={}, smsCode={}", phone, smsCode);

        Long tenantId = resolveTenantId();

        // 用手机号作为 username
        String username = "phone_" + phone;
        SysUser user = sysUserMapper.selectByUsername(username, tenantId);

        if (user == null) {
            // 自动注册
            user = createWxUser(username, phone.substring(0, 3) + "****" + phone.substring(7), phone, tenantId);
            log.info("手机号用户自动注册: phone={}, userId={}", phone, user.getId());
        }

        return buildLoginResponse(user, tenantId);
    }

    /**
     * 创建 C 端用户（微信/手机号自动注册）
     */
    private SysUser createWxUser(String username, String nickname, String phone, Long tenantId) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setStatus(1);
        user.setTenantId(tenantId);
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return user;
    }

    /**
     * 构建 JWT 登录响应
     */
    private LoginResponse buildLoginResponse(SysUser user, Long tenantId) {
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), tenantId);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername(), tenantId);

        Tenant tenant = tenantService.getById(tenantId);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(System.currentTimeMillis() + 7200000L)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .tenantId(tenantId)
                .tenantName(tenant != null ? tenant.getTenantName() : "默认租户")
                .build();
    }

    /**
     * 解析租户 ID
     */
    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = Constants.DEFAULT_TENANT_ID;
        }
        TenantContext.setTenantId(tenantId);
        return tenantId;
    }
}
