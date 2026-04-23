package com.senyehui.tenant.filter;

import com.senyehui.common.constant.Constants;
import com.senyehui.tenant.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户解析过滤器 — 从请求中解析租户 ID 并设置到上下文
 * <p>
 * 解析优先级: Header > Request Parameter
 * JWT Token 中的 tenantId 由 JwtAuthenticationFilter 负责设置
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TenantResolveFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Long tenantId = resolveTenantId(request);
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
                log.debug("租户上下文已设置: tenantId={}", tenantId);
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * 解析租户 ID
     */
    private Long resolveTenantId(HttpServletRequest request) {
        // 1. 从 Header 中获取
        String tenantIdStr = request.getHeader(Constants.TENANT_HEADER);

        // 2. 从请求参数中获取
        if (!StringUtils.hasText(tenantIdStr)) {
            tenantIdStr = request.getParameter("tenantId");
        }

        if (StringUtils.hasText(tenantIdStr)) {
            try {
                return Long.parseLong(tenantIdStr);
            } catch (NumberFormatException e) {
                log.warn("租户 ID 格式错误: {}", tenantIdStr);
            }
        }
        return null;
    }
}
