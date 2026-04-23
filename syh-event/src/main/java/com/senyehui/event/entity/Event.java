package com.senyehui.event.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 赛事活动实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("event_info")
public class Event extends BaseEntity {

    /** 赛事标题 */
    private String title;

    /** 分类 ID */
    private Long categoryId;

    /** 赛事类型: 1-赛事 2-活动 3-培训 4-露营 */
    private Integer eventType;

    /** 封面图 */
    private String coverImage;

    /** 详情描述（富文本） */
    private String description;

    /** 活动开始时间 */
    private LocalDateTime startTime;

    /** 活动结束时间 */
    private LocalDateTime endTime;

    /** 报名开始时间 */
    private LocalDateTime regStartTime;

    /** 报名截止时间 */
    private LocalDateTime regEndTime;

    /** 活动地点（简称） */
    private String location;

    /** 详细地址 */
    private String address;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 最大参与人数 (0=不限) */
    private Integer maxParticipants;

    /** 当前报名人数 */
    private Integer currentParticipants;

    /** 报名费 */
    private BigDecimal price;

    /** 状态: 0-草稿 1-待审核 2-已发布 3-报名中 4-进行中 5-已结束 6-已取消 */
    private Integer status;

    /** 排序权重 */
    private Integer sortOrder;

    /** 浏览量 */
    private Integer viewCount;

    /** 关联报名表单 ID */
    private Long formTemplateId;
}
