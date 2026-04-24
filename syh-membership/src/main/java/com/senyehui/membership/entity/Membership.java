package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership")
public class Membership extends BaseEntity {

    private Long userId;

    private String membershipType;

    private String level;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean autoRenew;

    private BigDecimal price;

    private String billingCycle;

    private Integer points;
}
