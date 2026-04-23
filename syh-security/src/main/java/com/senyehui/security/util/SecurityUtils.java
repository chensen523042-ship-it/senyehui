package com.senyehui.security.util;

import com.senyehui.common.constant.Constants;
import com.senyehui.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类 — 获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    private static JwtTokenProvider jwtTokenProvider;

    public SecurityUtils(JwtTokenProvider provider) {
        SecurityUtils.jwtTokenProvider = provider;
    }

    /**
     * 获取当前登录用户 ID（从 JWT Token 中提取）
     */
    public static Long getCurrentUserId() {
        String token = resolveTokenFromRequest();
        if (token != null && jwtTokenProvider != null) {
            try {
                return jwtTokenProvider.getUserId(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        return null;
    }

    /**
     * 从当前 HTTP 请求头中提取 JWT Token
     */
    private static String resolveTokenFromRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        String bearer = request.getHeader(Constants.TOKEN_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.TOKEN_PREFIX)) {
            return bearer.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
