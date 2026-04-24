package com.senyehui.membership.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyehui.membership.entity.MembershipConfig;
import com.senyehui.membership.entity.MembershipOrder;
import com.senyehui.membership.mapper.MembershipOrderMapper;
import com.senyehui.membership.service.MembershipConfigService;
import com.senyehui.membership.service.MembershipOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MembershipOrderServiceImpl extends ServiceImpl<MembershipOrderMapper, MembershipOrder> implements MembershipOrderService {

    private final MembershipConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipOrder createOrder(Long userId, Long membershipId, Long configId, String orderType) {
        MembershipConfig config = configService.getById(configId);
        if (config == null) {
            throw new RuntimeException("会员配置不存在");
        }

        MembershipOrder order = new MembershipOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMembershipId(membershipId);
        order.setConfigId(configId);
        order.setMembershipType(config.getMembershipType());
        order.setLevel(config.getLevel());
        order.setOrderType(orderType);
        order.setAmount(config.getPrice());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(config.getPrice());
        order.setStatus("PENDING");

        save(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderNo) {
        MembershipOrder order = lambdaQuery()
                .eq(MembershipOrder::getOrderNo, orderNo)
                .one();

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("PAID");
        order.setPaymentTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        MembershipOrder order = lambdaQuery()
                .eq(MembershipOrder::getOrderNo, orderNo)
                .one();

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }

        order.setStatus("CANCELLED");
        updateById(order);
    }

    private String generateOrderNo() {
        return "MO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", (int)(Math.random() * 1000000));
    }
}
