package com.senyehui.registration.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.registration.dto.RegisterRequest;
import com.senyehui.registration.entity.Registration;
import com.senyehui.registration.service.RegistrationService;
import com.senyehui.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端报名 API（需要登录）
 */
@RestController
@RequestMapping("/open/registration")
@RequiredArgsConstructor
public class OpenRegistrationController {

    private final RegistrationService registrationService;

    /**
     * 提交报名
     */
    @PostMapping("/submit")
    public R<Registration> submit(@RequestBody RegisterRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        Registration reg = registrationService.register(req, userId);
        return R.ok(reg);
    }

    /**
     * 我的报名列表
     */
    @GetMapping("/my")
    public R<PageResult<Registration>> myRegistrations(PageQuery pageQuery) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<Registration> result = registrationService.pageByUser(pageQuery, userId);
        return R.ok(result);
    }

    /**
     * 报名详情
     */
    @GetMapping("/{id}")
    public R<Registration> detail(@PathVariable Long id) {
        return R.ok(registrationService.getById(id));
    }

    /**
     * 取消报名
     */
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return R.ok();
    }
}
