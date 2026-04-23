package com.senyehui.security.service;

import com.senyehui.common.constant.Constants;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.ResultCode;
import com.senyehui.security.dto.LoginRequest;
import com.senyehui.security.dto.LoginResponse;
import com.senyehui.security.dto.UserInfoResponse;
import com.senyehui.security.entity.SysUser;
import com.senyehui.security.jwt.JwtTokenProvider;
import com.senyehui.security.mapper.SysUserMapper;
import com.senyehui.tenant.context.TenantContext;
import com.senyehui.tenant.entity.Tenant;
import com.senyehui.tenant.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 设置租户上下文
        Long tenantId = request.getTenantId();
        if (tenantId == null) {
            tenantId = TenantContext.getTenantId();
        }
        if (tenantId == null) {
            tenantId = Constants.DEFAULT_TENANT_ID;
        }
        TenantContext.setTenantId(tenantId);

        // 校验租户
        if (!tenantService.isValid(tenantId)) {
            throw new BusinessException(ResultCode.TENANT_DISABLED);
        }

        // 查询用户
        SysUser user = sysUserMapper.selectByUsername(request.getUsername(), tenantId);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 生成 Token
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), tenantId);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername(), tenantId);

        // 获取租户信息
        Tenant tenant = tenantService.getById(tenantId);

        log.info("用户登录成功: username={}, tenantId={}", request.getUsername(), tenantId);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(System.currentTimeMillis() + 7200000L)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .tenantId(tenantId)
                .tenantName(tenant.getTenantName())
                .build();
    }

    /**
     * 登出 — 将 Token 加入黑名单
     */
    public void logout(HttpServletRequest request) {
        String bearer = request.getHeader(Constants.TOKEN_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.TOKEN_PREFIX)) {
            String token = bearer.substring(Constants.TOKEN_PREFIX.length());
            jwtTokenProvider.blacklistToken(token);
            log.info("用户登出成功");
        }
    }

    /**
     * 刷新 Token
     */
    public LoginResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);
        Long tenantId = jwtTokenProvider.getTenantId(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, username, tenantId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, username, tenantId);

        // 将旧 refreshToken 加入黑名单
        jwtTokenProvider.blacklistToken(refreshToken);

        Tenant tenant = tenantService.getById(tenantId);
        TenantContext.setTenantId(tenantId);
        SysUser user = sysUserMapper.selectByUsername(username, tenantId);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(System.currentTimeMillis() + 7200000L)
                .userId(userId)
                .username(username)
                .nickname(user != null ? user.getNickname() : username)
                .avatar(user != null ? user.getAvatar() : null)
                .tenantId(tenantId)
                .tenantName(tenant.getTenantName())
                .build();
    }

    /**
     * 获取当前用户信息
     */
    public UserInfoResponse getCurrentUser(String username) {
        Long tenantId = TenantContext.getTenantId();
        SysUser user = sysUserMapper.selectByUsername(username, tenantId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        List<String> roles = sysUserMapper.selectRoleCodes(user.getId());
        List<String> permissions = sysUserMapper.selectPermissions(user.getId());

        Tenant tenant = tenantService.getById(tenantId);

        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setTenantId(tenantId);
        response.setTenantName(tenant.getTenantName());
        response.setRoles(roles);
        response.setPermissions(permissions);
        return response;
    }
}
