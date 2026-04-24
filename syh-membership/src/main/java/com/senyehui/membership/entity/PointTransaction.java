package com.senyehui.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_transaction")
public class PointTransaction extends BaseEntity {

    private Long userId;

    private String transactionType;

    private BigDecimal points;

    private String description;

    private String relatedType;

    private Long relatedId;

    private LocalDateTime expireTime;
}
