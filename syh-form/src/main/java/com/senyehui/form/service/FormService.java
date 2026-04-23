package com.senyehui.form.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.form.engine.FormRenderEngine;
import com.senyehui.form.engine.FormValidator;
import com.senyehui.form.entity.FormData;
import com.senyehui.form.entity.FormField;
import com.senyehui.form.entity.FormTemplate;
import com.senyehui.form.mapper.FormDataMapper;
import com.senyehui.form.mapper.FormFieldMapper;
import com.senyehui.form.mapper.FormTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 表单服务（模板管理 + 字段管理 + 数据提交）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormService {

    private final FormTemplateMapper templateMapper;
    private final FormFieldMapper fieldMapper;
    private final FormDataMapper dataMapper;
    private final FormRenderEngine renderEngine;
    private final FormValidator formValidator;

    // ==================== 模板管理 ====================

    /**
     * 分页查询表单模板
     */
    public PageResult<FormTemplate> pageTemplates(PageQuery pageQuery, String keyword) {
        LambdaQueryWrapper<FormTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(FormTemplate::getName, keyword);
        }
        wrapper.orderByDesc(FormTemplate::getCreateTime);

        IPage<FormTemplate> page = templateMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 获取模板详情
     */
    public FormTemplate getTemplateById(Long id) {
        FormTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException("表单模板不存在");
        }
        return template;
    }

    /**
     * 创建模板
     */
    @Transactional(rollbackFor = Exception.class)
    public FormTemplate createTemplate(FormTemplate template) {
        if (template.getStatus() == null) {
            template.setStatus(1);
        }
        templateMapper.insert(template);
        log.info("创建表单模板: id={}, name={}", template.getId(), template.getName());
        return template;
    }

    /**
     * 更新模板
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(FormTemplate template) {
        getTemplateById(template.getId());
        templateMapper.updateById(template);
        log.info("更新表单模板: id={}", template.getId());
    }

    /**
     * 删除模板（同步删除字段）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        getTemplateById(id);
        // 删除关联字段
        fieldMapper.delete(new LambdaQueryWrapper<FormField>().eq(FormField::getTemplateId, id));
        templateMapper.deleteById(id);
        log.info("删除表单模板及字段: templateId={}", id);
    }

    // ==================== 字段管理 ====================

    /**
     * 查询模板下所有字段（按排序）
     */
    public List<FormField> listFieldsByTemplateId(Long templateId) {
        return fieldMapper.selectList(
                new LambdaQueryWrapper<FormField>()
                        .eq(FormField::getTemplateId, templateId)
                        .orderByAsc(FormField::getSortOrder)
        );
    }

    /**
     * 批量保存字段（先删后插 — 覆盖式更新）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveFields(Long templateId, List<FormField> fields) {
        getTemplateById(templateId); // 确认模板存在
        // 删除旧字段
        fieldMapper.delete(new LambdaQueryWrapper<FormField>().eq(FormField::getTemplateId, templateId));
        // 插入新字段
        int sortOrder = 0;
        for (FormField field : fields) {
            field.setId(null); // 确保新生成 ID
            field.setTemplateId(templateId);
            field.setSortOrder(sortOrder++);
            fieldMapper.insert(field);
        }
        log.info("保存表单字段: templateId={}, fieldCount={}", templateId, fields.size());
    }

    /**
     * 添加单个字段
     */
    @Transactional(rollbackFor = Exception.class)
    public FormField addField(FormField field) {
        getTemplateById(field.getTemplateId());
        if (field.getSortOrder() == null) {
            field.setSortOrder(0);
        }
        if (field.getRequired() == null) {
            field.setRequired(0);
        }
        if (field.getVisible() == null) {
            field.setVisible(1);
        }
        fieldMapper.insert(field);
        log.info("添加表单字段: id={}, templateId={}, key={}", field.getId(), field.getTemplateId(), field.getFieldKey());
        return field;
    }

    /**
     * 更新单个字段
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateField(FormField field) {
        FormField existing = fieldMapper.selectById(field.getId());
        if (existing == null) {
            throw new BusinessException("表单字段不存在");
        }
        fieldMapper.updateById(field);
        log.info("更新表单字段: id={}", field.getId());
    }

    /**
     * 删除单个字段
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteField(Long id) {
        fieldMapper.deleteById(id);
        log.info("删除表单字段: id={}", id);
    }

    // ==================== 渲染 & 校验 & 数据提交 ====================

    /**
     * 渲染表单（返回前端消费的 JSON Schema）
     */
    public Map<String, Object> renderForm(Long templateId) {
        getTemplateById(templateId);
        List<FormField> fields = listFieldsByTemplateId(templateId);
        return renderEngine.render(fields);
    }

    /**
     * 提交表单数据（含校验）
     */
    @Transactional(rollbackFor = Exception.class)
    public FormData submitFormData(Long templateId, Long eventId, Long userId, Map<String, Object> formValues) {
        getTemplateById(templateId);
        List<FormField> fields = listFieldsByTemplateId(templateId);

        // 校验
        formValidator.validateAndThrow(fields, formValues);

        // 保存数据
        FormData formData = new FormData();
        formData.setTemplateId(templateId);
        formData.setEventId(eventId);
        formData.setUserId(userId);
        formData.setFormValues(formValues);
        dataMapper.insert(formData);

        log.info("提交表单数据: id={}, templateId={}, eventId={}", formData.getId(), templateId, eventId);
        return formData;
    }

    /**
     * 查询表单提交数据
     */
    public PageResult<FormData> pageFormData(PageQuery pageQuery, Long templateId, Long eventId) {
        LambdaQueryWrapper<FormData> wrapper = new LambdaQueryWrapper<>();
        if (templateId != null) {
            wrapper.eq(FormData::getTemplateId, templateId);
        }
        if (eventId != null) {
            wrapper.eq(FormData::getEventId, eventId);
        }
        wrapper.orderByDesc(FormData::getCreateTime);

        IPage<FormData> page = dataMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
