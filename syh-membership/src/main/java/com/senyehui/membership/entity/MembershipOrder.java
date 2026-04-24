package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership_order")
public class MembershipOrder extends BaseEntity {

    private String orderNo;

    private Long userId;

    private Long membershipId;

    private Long configId;

    private String membershipType;

    private String level;

    private String orderType;

    private BigDecimal amount;

    private BigDecimal discountAmount;

    private BigDecimal actualAmount;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private String status;
}
