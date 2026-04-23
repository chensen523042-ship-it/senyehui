package com.senyehui.registration.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报名记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("registration")
public class Registration extends BaseEntity {

    /** 赛事 ID */
    private Long eventId;

    /** 组别 ID */
    private Long groupId;

    /** 报名用户 ID */
    private Long userId;

    /** 报名编号 */
    private String regNo;

    /** 参赛者姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 身份证号（加密存储） */
    private String idCard;

    /** 关联表单数据 ID */
    private Long formDataId;

    /** 实付金额 */
    private BigDecimal amount;

    /** 支付状态: 0-未支付 1-已支付 2-已退款 */
    private Integer payStatus;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 支付订单号 */
    private String payOrderNo;

    /** 状态: 0-待审核 1-已通过 2-已拒绝 3-已取消 */
    private Integer status;

    /** 签到状态: 0-未签到 1-已签到 */
    private Integer checkInStatus;

    /** 签到时间 */
    private LocalDateTime checkInTime;
}
