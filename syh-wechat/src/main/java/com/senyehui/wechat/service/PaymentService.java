package com.senyehui.wechat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.registration.entity.Registration;
import com.senyehui.registration.mapper.RegistrationMapper;
import com.senyehui.wechat.entity.PayOrder;
import com.senyehui.wechat.mapper.PayOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PayOrderMapper payOrderMapper;
    private final RegistrationMapper registrationMapper;

    @Transactional(rollbackFor = Exception.class)
    public PayOrder createOrder(Long registrationId, Long userId) {
        Registration reg = registrationMapper.selectById(registrationId);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (reg.getPayStatus() != null && reg.getPayStatus() == 1) {
            throw new BusinessException("已支付，无需重复支付");
        }
        if (reg.getAmount() == null || reg.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("免费赛事无需支付");
        }

        // 检查是否已有待支付订单
        QueryWrapper<PayOrder> qw = new QueryWrapper<>();
        qw.eq("registration_id", registrationId)
          .eq("status", 0)
          .gt("expire_time", LocalDateTime.now());
        PayOrder existing = payOrderMapper.selectOne(qw);
        if (existing != null) {
            return existing;
        }

        PayOrder order = new PayOrder();
        order.setOrderNo(generateOrderNo());
        order.setRegistrationId(registrationId);
        order.setEventId(reg.getEventId());
        order.setUserId(userId);
        order.setAmount(reg.getAmount());
        order.setStatus(0);
        order.setPayChannel("mock");
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        payOrderMapper.insert(order);

        log.info("创建支付订单: orderNo={}, amount={}", order.getOrderNo(), order.getAmount());
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public PayOrder mockPay(String orderNo) {
        PayOrder order = getByOrderNo(orderNo);
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态异常，无法支付");
        }
        if (order.getExpireTime() != null && order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("订单已过期");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId("MOCK_" + System.currentTimeMillis());
        order.setPayChannel("mock");
        payOrderMapper.updateById(order);

        Registration reg = registrationMapper.selectById(order.getRegistrationId());
        if (reg != null) {
            reg.setPayStatus(1);
            reg.setPayTime(LocalDateTime.now());
            reg.setPayOrderNo(order.getOrderNo());
            registrationMapper.updateById(reg);
        }

        log.info("模拟支付成功: orderNo={}, amount={}", orderNo, order.getAmount());
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public PayOrder refund(String orderNo) {
        PayOrder order = getByOrderNo(orderNo);
        if (order.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单可以退款");
        }

        order.setStatus(3);
        order.setRefundTime(LocalDateTime.now());
        order.setRefundAmount(order.getAmount());
        payOrderMapper.updateById(order);

        Registration reg = registrationMapper.selectById(order.getRegistrationId());
        if (reg != null) {
            reg.setPayStatus(2);
            registrationMapper.updateById(reg);
        }

        log.info("退款成功: orderNo={}, amount={}", orderNo, order.getAmount());
        return order;
    }

    public PayOrder getByOrderNo(String orderNo) {
        QueryWrapper<PayOrder> qw = new QueryWrapper<>();
        qw.eq("order_no", orderNo);
        PayOrder order = payOrderMapper.selectOne(qw);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    public PayOrder getByRegistrationId(Long registrationId) {
        QueryWrapper<PayOrder> qw = new QueryWrapper<>();
        qw.eq("registration_id", registrationId)
          .orderByDesc("create_time")
          .last("LIMIT 1");
        return payOrderMapper.selectOne(qw);
    }

    public BigDecimal getTotalIncome(Long tenantId) {
        return payOrderMapper.sumPaidAmount(tenantId);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "PAY" + date + random;
    }
}
