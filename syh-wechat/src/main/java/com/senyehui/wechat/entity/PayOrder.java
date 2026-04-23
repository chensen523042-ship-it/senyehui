package com.senyehui.wechat.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 */
@Data
@TableName("pay_order")
public class PayOrder extends BaseEntity {

    /** 支付订单号 */
    private String orderNo;

    /** 关联报名 ID */
    private Long registrationId;

    /** 赛事 ID */
    private Long eventId;

    /** 用户 ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 状态: 0-待支付 1-已支付 2-已取消 3-已退款 */
    private Integer status;

    /** 支付渠道: mock/wxpay/alipay */
    private String payChannel;

    /** 第三方交易号 */
    private String transactionId;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 退款时间 */
    private LocalDateTime refundTime;

    /** 退款金额 */
    private BigDecimal refundAmount;
}
