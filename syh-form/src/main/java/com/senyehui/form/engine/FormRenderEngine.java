package com.senyehui.form.engine;

import com.senyehui.form.entity.FormField;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 表单渲染引擎
 * 将模板字段定义转换为前端可消费的 JSON Schema 格式
 */
@Component
public class FormRenderEngine {

    /**
     * 将字段列表渲染为 JSON Schema
     * 返回格式:
     * {
     *   "fields": [
     *     {
     *       "key": "name",
     *       "label": "姓名",
     *       "type": "text",
     *       "required": true,
     *       "placeholder": "请输入姓名",
     *       "defaultValue": "",
     *       "options": [...],
     *       "rules": {...}
     *     }
     *   ]
     * }
     */
    public Map<String, Object> render(List<FormField> fields) {
        Map<String, Object> schema = new LinkedHashMap<>();
        List<Map<String, Object>> fieldSchemas = new ArrayList<>();

        for (FormField field : fields) {
            if (field.getVisible() != null && field.getVisible() == 0) {
                continue; // 跳过不可见字段
            }
            fieldSchemas.add(renderField(field));
        }

        schema.put("fields", fieldSchemas);
        return schema;
    }

    /**
     * 渲染单个字段为前端 JSON Schema
     */
    private Map<String, Object> renderField(FormField field) {
        Map<String, Object> fieldSchema = new LinkedHashMap<>();
        fieldSchema.put("key", field.getFieldKey());
        fieldSchema.put("label", field.getFieldLabel());
        fieldSchema.put("type", field.getFieldType());
        fieldSchema.put("required", field.getRequired() != null && field.getRequired() == 1);
        fieldSchema.put("placeholder", field.getPlaceholder());
        fieldSchema.put("defaultValue", field.getDefaultValue());

        // 选项类字段（select/radio/checkbox）
        if (field.getOptions() != null && !field.getOptions().isEmpty()) {
            fieldSchema.put("options", field.getOptions());
        }

        // 校验规则
        if (field.getValidationRules() != null && !field.getValidationRules().isEmpty()) {
            fieldSchema.put("rules", field.getValidationRules());
        }

        return fieldSchema;
    }
}
