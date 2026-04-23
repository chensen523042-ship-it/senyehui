package com.senyehui.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限/菜单资源实体（全局，不按租户隔离）
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 父级 ID */
    private Long parentId;

    /** 权限名称 */
    private String name;

    /** 权限标识 (如 system:user:list) */
    private String perms;

    /** 类型: 1-目录 2-菜单 3-按钮 */
    private Integer type;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sortOrder;

    /** 是否可见: 0-隐藏 1-显示 */
    private Integer visible;

    /** 状态: 0-禁用 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
