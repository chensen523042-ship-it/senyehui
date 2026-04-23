package com.senyehui.tenant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户实体
 */
@Data
@TableName("sys_tenant")
public class Tenant implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 租户编码 */
    private String tenantCode;

    /** 租户名称（组织者名称） */
    private String tenantName;

    /** 自定义域名 */
    private String domain;

    /** Logo URL */
    private String logoUrl;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 联系邮箱 */
    private String contactEmail;

    /** 套餐 ID */
    private Long packageId;

    /** 状态: 0-停用 1-正常 2-试用 */
    private Integer status;

    /** 套餐到期时间 */
    private LocalDateTime expireTime;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
