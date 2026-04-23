package com.senyehui.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 排序 */
    private Integer sortOrder;

    /** 状态: 0-禁用 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
