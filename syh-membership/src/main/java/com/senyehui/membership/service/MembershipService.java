package com.senyehui.membership.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyehui.membership.entity.Membership;

public interface MembershipService extends IService<Membership> {

    Membership getUserMembership(Long userId);

    Membership purchaseMembership(Long userId, Long configId);

    Membership renewMembership(Long membershipId);

    Membership upgradeMembership(Long membershipId, String targetLevel);

    void cancelMembership(Long membershipId);

    boolean checkMembershipValid(Long userId);
}
