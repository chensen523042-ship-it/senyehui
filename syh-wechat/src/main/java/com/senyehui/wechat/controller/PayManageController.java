package com.senyehui.wechat.controller;

import com.senyehui.common.model.R;
import com.senyehui.wechat.entity.PayOrder;
import com.senyehui.wechat.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端支付管理 API
 */
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayManageController {

    private final PaymentService paymentService;

    /**
     * 退款
     */
    @PostMapping("/refund")
    public R<PayOrder> refund(@RequestParam String orderNo) {
        return R.ok(paymentService.refund(orderNo));
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/order/{orderNo}")
    public R<PayOrder> getOrder(@PathVariable String orderNo) {
        return R.ok(paymentService.getByOrderNo(orderNo));
    }

    /**
     * 收入统计
     */
    @GetMapping("/income")
    public R<java.math.BigDecimal> getIncome(@RequestParam(defaultValue = "1") Long tenantId) {
        return R.ok(paymentService.getTotalIncome(tenantId));
    }
}
