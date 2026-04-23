package com.senyehui.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.senyehui.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 表单提交数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "form_data", autoResultMap = true)
public class FormData extends BaseEntity {

    /** 模板 ID */
    private Long templateId;

    /** 赛事 ID */
    private Long eventId;

    /** 提交用户 ID */
    private Long userId;

    /** 表单数据: {fieldKey: value} */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> formValues;
}
