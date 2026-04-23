package com.senyehui.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 表单字段定义实体
 */
@Data
@TableName(value = "form_field", autoResultMap = true)
public class FormField implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 模板 ID */
    private Long templateId;

    /** 字段标识（唯一键） */
    private String fieldKey;

    /** 字段标签 */
    private String fieldLabel;

    /** 字段类型: text/number/select/radio/checkbox/date/file/phone/idcard */
    private String fieldType;

    /** 占位提示 */
    private String placeholder;

    /** 默认值 */
    private String defaultValue;

    /** 选项列表: [{label, value}] */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, String>> options;

    /** 校验规则: {required, min, max, pattern, message} */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> validationRules;

    /** 排序 */
    private Integer sortOrder;

    /** 是否必填 */
    private Integer required;

    /** 是否可见 */
    private Integer visible;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
