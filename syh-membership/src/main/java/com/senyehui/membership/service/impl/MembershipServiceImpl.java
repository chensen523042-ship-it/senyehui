package com.senyehui.membership.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyehui.membership.entity.Membership;
import com.senyehui.membership.entity.MembershipConfig;
import com.senyehui.membership.entity.MembershipOrder;
import com.senyehui.membership.enums.MembershipStatus;
import com.senyehui.membership.mapper.MembershipMapper;
import com.senyehui.membership.service.MembershipConfigService;
import com.senyehui.membership.service.MembershipOrderService;
import com.senyehui.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership> implements MembershipService {

    private final MembershipConfigService configService;
    private final MembershipOrderService orderService;

    @Override
    public Membership getUserMembership(Long userId) {
        return lambdaQuery()
                .eq(Membership::getUserId, userId)
                .eq(Membership::getStatus, MembershipStatus.ACTIVE.getCode())
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Membership purchaseMembership(Long userId, Long configId) {
        MembershipConfig config = configService.getById(configId);
        if (config == null) {
            throw new RuntimeException("会员配置不存在");
        }

        Membership existing = getUserMembership(userId);
        if (existing != null) {
            throw new RuntimeException("用户已有有效会员");
        }

        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setMembershipType(config.getMembershipType());
        membership.setLevel(config.getLevel());
        membership.setStatus(MembershipStatus.ACTIVE.getCode());
        membership.setStartTime(LocalDateTime.now());
        membership.setEndTime(LocalDateTime.now().plusDays(config.getDurationDays()));
        membership.setPoints(0);
        membership.setAutoRenew(false);

        save(membership);

        orderService.createOrder(userId, membership.getId(), configId, "NEW");

        return membership;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Membership renewMembership(Long membershipId) {
        Membership membership = getById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员不存在");
        }

        MembershipConfig config = configService.lambdaQuery()
                .eq(MembershipConfig::getMembershipType, membership.getMembershipType())
                .eq(MembershipConfig::getLevel, membership.getLevel())
                .one();

        if (config == null) {
            throw new RuntimeException("会员配置不存在");
        }

        LocalDateTime newEndTime = membership.getEndTime().isAfter(LocalDateTime.now())
                ? membership.getEndTime().plusDays(config.getDurationDays())
                : LocalDateTime.now().plusDays(config.getDurationDays());

        membership.setEndTime(newEndTime);
        membership.setStatus(MembershipStatus.ACTIVE.getCode());
        updateById(membership);

        orderService.createOrder(membership.getUserId(), membershipId, config.getId(), "RENEW");

        return membership;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Membership upgradeMembership(Long membershipId, String targetLevel) {
        Membership membership = getById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员不存在");
        }

        MembershipConfig config = configService.lambdaQuery()
                .eq(MembershipConfig::getMembershipType, membership.getMembershipType())
                .eq(MembershipConfig::getLevel, targetLevel)
                .one();

        if (config == null) {
            throw new RuntimeException("目标会员配置不存在");
        }

        membership.setLevel(targetLevel);
        updateById(membership);

        orderService.createOrder(membership.getUserId(), membershipId, config.getId(), "UPGRADE");

        return membership;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMembership(Long membershipId) {
        Membership membership = getById(membershipId);
        if (membership == null) {
            throw new RuntimeException("会员不存在");
        }

        membership.setStatus(MembershipStatus.CANCELLED.getCode());
        updateById(membership);
    }

    @Override
    public boolean checkMembershipValid(Long userId) {
        Membership membership = getUserMembership(userId);
        if (membership == null) {
            return false;
        }

        if (membership.getEndTime() != null && membership.getEndTime().isBefore(LocalDateTime.now())) {
            membership.setStatus(MembershipStatus.EXPIRED.getCode());
            updateById(membership);
            return false;
        }

        return true;
    }
}
