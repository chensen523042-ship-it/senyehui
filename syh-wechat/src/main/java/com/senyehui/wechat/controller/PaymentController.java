package com.senyehui.wechat.controller;

import com.senyehui.common.model.R;
import com.senyehui.wechat.entity.PayOrder;
import com.senyehui.wechat.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端支付 API
 */
@RestController
@RequestMapping("/open/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public R<PayOrder> createOrder(@RequestParam Long registrationId,
                                    @RequestParam(required = false) Long userId) {
        return R.ok(paymentService.createOrder(registrationId, userId));
    }

    /**
     * 模拟支付（开发模式）
     */
    @PostMapping("/mock-pay")
    public R<PayOrder> mockPay(@RequestParam String orderNo) {
        return R.ok(paymentService.mockPay(orderNo));
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{orderNo}")
    public R<PayOrder> getStatus(@PathVariable String orderNo) {
        return R.ok(paymentService.getByOrderNo(orderNo));
    }

    /**
     * 根据报名ID查询订单
     */
    @GetMapping("/order/{registrationId}")
    public R<PayOrder> getByRegistration(@PathVariable Long registrationId) {
        return R.ok(paymentService.getByRegistrationId(registrationId));
    }
}
