package com.senyehui.event.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 赛事组别实体（如全马、半马、迷你马拉松等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("event_group")
public class EventGroup extends BaseEntity {

    /** 关联赛事 ID */
    private Long eventId;

    /** 组别名称 */
    @TableField("group_name")
    private String name;

    /** 组别描述 */
    private String description;

    /** 最大参与人数 (0=不限) */
    private Integer maxParticipants;

    /** 当前报名人数 */
    private Integer currentParticipants;

    /** 报名费 */
    private BigDecimal price;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态: 0-停用 1-正常 */
    private Integer status;
}
