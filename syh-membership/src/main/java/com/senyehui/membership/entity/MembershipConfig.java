package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership_config")
public class MembershipConfig extends BaseEntity {

    private String membershipType;

    private String level;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer durationDays;

    private String benefits;

    private Integer sortOrder;

    private Boolean enabled;
}
