package com.senyehui.registration.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.registration.dto.RegisterRequest;
import com.senyehui.registration.entity.Registration;
import com.senyehui.registration.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 报名管理 API
 */
@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * 提交报名
     */
    @PostMapping("/register")
    public R<Registration> register(@RequestBody RegisterRequest request,
                                     @RequestParam(required = false) Long userId) {
        return R.ok(registrationService.register(request, userId));
    }

    /**
     * 取消报名
     */
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return R.ok();
    }

    /**
     * 审核报名
     */
    @PutMapping("/{id}/audit/{status}")
    public R<Void> audit(@PathVariable Long id, @PathVariable Integer status) {
        registrationService.audit(id, status);
        return R.ok();
    }

    /**
     * 签到
     */
    @PutMapping("/{id}/check-in")
    public R<Void> checkIn(@PathVariable Long id) {
        registrationService.checkIn(id);
        return R.ok();
    }

    /**
     * 分页查询报名列表
     */
    @GetMapping("/page")
    public R<PageResult<Registration>> page(PageQuery pageQuery,
                                             @RequestParam(required = false) Long eventId,
                                             @RequestParam(required = false) Long groupId,
                                             @RequestParam(required = false) Integer status) {
        return R.ok(registrationService.page(pageQuery, eventId, groupId, status));
    }

    /**
     * 获取报名详情
     */
    @GetMapping("/{id}")
    public R<Registration> getById(@PathVariable Long id) {
        return R.ok(registrationService.getById(id));
    }
}
