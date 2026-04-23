package com.senyehui.form.engine;

import com.senyehui.common.exception.BusinessException;
import com.senyehui.form.entity.FormField;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表单校验引擎
 * 根据字段定义的 validationRules 校验提交数据
 */
@Component
public class FormValidator {

    /**
     * 校验表单提交数据
     *
     * @param fields     字段定义列表
     * @param formValues 提交的表单数据 {fieldKey: value}
     * @return 校验错误列表，空列表表示校验通过
     */
    public List<String> validate(List<FormField> fields, Map<String, Object> formValues) {
        List<String> errors = new ArrayList<>();

        for (FormField field : fields) {
            // 跳过隐藏字段
            if (field.getVisible() != null && field.getVisible() == 0) {
                continue;
            }

            String key = field.getFieldKey();
            Object value = formValues.get(key);
            String strValue = value != null ? value.toString().trim() : "";

            // 必填校验
            if (field.getRequired() != null && field.getRequired() == 1) {
                if (!StringUtils.hasText(strValue)) {
                    errors.add(field.getFieldLabel() + "不能为空");
                    continue; // 必填未填，后续校验无意义
                }
            } else {
                // 非必填字段，如果没有值则跳过后续校验
                if (!StringUtils.hasText(strValue)) {
                    continue;
                }
            }

            // 按字段类型校验
            validateByType(field, strValue, errors);

            // 自定义校验规则
            if (field.getValidationRules() != null) {
                validateByRules(field, strValue, errors);
            }
        }

        return errors;
    }

    /**
     * 校验并抛出异常（如果有错误）
     */
    public void validateAndThrow(List<FormField> fields, Map<String, Object> formValues) {
        List<String> errors = validate(fields, formValues);
        if (!errors.isEmpty()) {
            throw new BusinessException("表单校验失败: " + String.join("; ", errors));
        }
    }

    /**
     * 按字段类型做固有校验
     */
    private void validateByType(FormField field, String value, List<String> errors) {
        switch (field.getFieldType()) {
            case "number" -> {
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    errors.add(field.getFieldLabel() + "必须是数字");
                }
            }
            case "phone" -> {
                if (!Pattern.matches("^1[3-9]\\d{9}$", value)) {
                    errors.add(field.getFieldLabel() + "手机号格式不正确");
                }
            }
            case "idcard" -> {
                if (!Pattern.matches("^\\d{17}[\\dXx]$", value)) {
                    errors.add(field.getFieldLabel() + "身份证号格式不正确");
                }
            }
            case "date" -> {
                // 日期格式基本校验
                if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}.*", value)) {
                    errors.add(field.getFieldLabel() + "日期格式不正确");
                }
            }
            case "select", "radio" -> {
                // 校验提交值是否在选项范围内
                if (field.getOptions() != null && !field.getOptions().isEmpty()) {
                    Set<String> validValues = field.getOptions().stream()
                            .map(opt -> opt.get("value"))
                            .collect(Collectors.toSet());
                    if (!validValues.contains(value)) {
                        errors.add(field.getFieldLabel() + "选择了无效的值");
                    }
                }
            }
            case "checkbox" -> {
                // checkbox 的值通常是逗号分隔的多选值
                if (field.getOptions() != null && !field.getOptions().isEmpty()) {
                    Set<String> validValues = field.getOptions().stream()
                            .map(opt -> opt.get("value"))
                            .collect(Collectors.toSet());
                    String[] selectedValues = value.split(",");
                    for (String sv : selectedValues) {
                        if (!validValues.contains(sv.trim())) {
                            errors.add(field.getFieldLabel() + "包含无效的选项: " + sv.trim());
                        }
                    }
                }
            }
            default -> {
                // text, file 等不做额外类型校验
            }
        }
    }

    /**
     * 按自定义校验规则校验
     */
    private void validateByRules(FormField field, String value, List<String> errors) {
        Map<String, Object> rules = field.getValidationRules();

        // 最小长度
        Object minObj = rules.get("min");
        if (minObj != null) {
            int min = ((Number) minObj).intValue();
            if (value.length() < min) {
                String msg = rules.containsKey("message")
                        ? rules.get("message").toString()
                        : field.getFieldLabel() + "长度不能小于" + min;
                errors.add(msg);
            }
        }

        // 最大长度
        Object maxObj = rules.get("max");
        if (maxObj != null) {
            int max = ((Number) maxObj).intValue();
            if (value.length() > max) {
                String msg = rules.containsKey("message")
                        ? rules.get("message").toString()
                        : field.getFieldLabel() + "长度不能大于" + max;
                errors.add(msg);
            }
        }

        // 正则校验
        Object patternObj = rules.get("pattern");
        if (patternObj != null) {
            String pattern = patternObj.toString();
            if (!Pattern.matches(pattern, value)) {
                String msg = rules.containsKey("message")
                        ? rules.get("message").toString()
                        : field.getFieldLabel() + "格式不正确";
                errors.add(msg);
            }
        }
    }
}
