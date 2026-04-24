package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_points")
public class MemberPoints extends BaseEntity {

    private Long userId;

    private BigDecimal totalPoints;

    private BigDecimal availablePoints;

    private BigDecimal frozenPoints;
}
