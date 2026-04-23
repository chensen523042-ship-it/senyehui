package com.senyehui.security.controller;

import com.senyehui.common.model.R;
import com.senyehui.security.dto.LoginRequest;
import com.senyehui.security.dto.LoginResponse;
import com.senyehui.security.dto.UserInfoResponse;
import com.senyehui.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return R.ok();
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public R<LoginResponse> refresh(@RequestParam String refreshToken) {
        return R.ok(authService.refresh(refreshToken));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public R<UserInfoResponse> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return R.ok(authService.getCurrentUser(auth.getName()));
    }
}
