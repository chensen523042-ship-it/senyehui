package com.senyehui.event.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 赛事分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("event_category")
public class EventCategory extends BaseEntity {

    /** 分类名称 */
    private String name;

    /** 分类图标 */
    private String icon;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态: 0-停用 1-正常 */
    private Integer status;
}
