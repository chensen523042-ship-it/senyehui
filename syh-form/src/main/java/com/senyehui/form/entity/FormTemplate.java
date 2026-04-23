package com.senyehui.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("form_template")
public class FormTemplate extends BaseEntity {

    /** 模板名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 状态: 0-禁用 1-启用 */
    private Integer status;
}
