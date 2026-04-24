package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership_benefit")
public class MembershipBenefit extends BaseEntity {

    private Long membershipId;

    private String benefitType;

    private String benefitCode;

    private String benefitName;

    private Integer totalQuota;

    private Integer usedQuota;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;
}
