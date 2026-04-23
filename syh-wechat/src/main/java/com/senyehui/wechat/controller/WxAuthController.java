package com.senyehui.wechat.controller;

import com.senyehui.common.model.R;
import com.senyehui.security.dto.LoginResponse;
import com.senyehui.wechat.service.WxAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 微信/手机号登录 API（C 端）
 *
 * 模拟模式：开发阶段使用，无需真实微信/短信服务
 * - POST /open/auth/wx-login    微信授权登录
 * - POST /open/auth/phone-login 手机号验证码登录
 */
@RestController
@RequestMapping("/open/auth")
@RequiredArgsConstructor
public class WxAuthController {

    private final WxAuthService wxAuthService;

    /**
     * 微信授权登录
     *
     * @param code 微信授权 code（模拟模式任意值）
     */
    @PostMapping("/wx-login")
    public R<LoginResponse> wxLogin(@RequestParam String code) {
        return R.ok(wxAuthService.wxLogin(code));
    }

    /**
     * 手机号验证码登录
     *
     * @param phone   手机号
     * @param smsCode 短信验证码（模拟模式任意值）
     */
    @PostMapping("/phone-login")
    public R<LoginResponse> phoneLogin(@RequestParam String phone,
                                        @RequestParam(defaultValue = "123456") String smsCode) {
        return R.ok(wxAuthService.phoneLogin(phone, smsCode));
    }
}
