package com.senyehui.membership.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.model.R;
import com.senyehui.membership.entity.Membership;
import com.senyehui.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/page")
    public R<Page<Membership>> getMembershipPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status) {

        Page<Membership> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Membership> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Membership::getUserId, userId);
        }
        if (level != null && !level.isEmpty()) {
            wrapper.eq(Membership::getLevel, level);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Membership::getStatus, status);
        }

        wrapper.orderByDesc(Membership::getCreateTime);
        Page<Membership> result = membershipService.page(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/user/{userId}")
    public R<Membership> getUserMembership(@PathVariable Long userId) {
        Membership membership = membershipService.getUserMembership(userId);
        return R.ok(membership);
    }

    @PostMapping("/purchase")
    public R<Membership> purchaseMembership(@RequestParam Long userId, @RequestParam Long configId) {
        Membership membership = membershipService.purchaseMembership(userId, configId);
        return R.ok(membership);
    }

    @PostMapping("/renew/{membershipId}")
    public R<Membership> renewMembership(@PathVariable Long membershipId) {
        Membership membership = membershipService.renewMembership(membershipId);
        return R.ok(membership);
    }

    @PostMapping("/upgrade/{membershipId}")
    public R<Membership> upgradeMembership(@PathVariable Long membershipId, @RequestParam String targetLevel) {
        Membership membership = membershipService.upgradeMembership(membershipId, targetLevel);
        return R.ok(membership);
    }

    @PostMapping("/cancel/{membershipId}")
    public R<Void> cancelMembership(@PathVariable Long membershipId) {
        membershipService.cancelMembership(membershipId);
        return R.ok();
    }

    @GetMapping("/check/{userId}")
    public R<Boolean> checkMembershipValid(@PathVariable Long userId) {
        boolean valid = membershipService.checkMembershipValid(userId);
        return R.ok(valid);
    }
}
