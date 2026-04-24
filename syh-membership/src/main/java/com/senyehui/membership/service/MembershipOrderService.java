package com.senyehui.membership.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyehui.membership.entity.MembershipOrder;

public interface MembershipOrderService extends IService<MembershipOrder> {

    MembershipOrder createOrder(Long userId, Long membershipId, Long configId, String orderType);

    void payOrder(String orderNo);

    void cancelOrder(String orderNo);
}
