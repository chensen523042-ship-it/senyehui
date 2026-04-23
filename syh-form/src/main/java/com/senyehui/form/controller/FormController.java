package com.senyehui.form.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.form.entity.FormData;
import com.senyehui.form.entity.FormField;
import com.senyehui.form.entity.FormTemplate;
import com.senyehui.form.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单管理 API
 */
@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    // ==================== 模板管理 ====================

    /**
     * 分页查询表单模板
     */
    @GetMapping("/template/page")
    public R<PageResult<FormTemplate>> pageTemplates(PageQuery pageQuery,
                                                      @RequestParam(required = false) String keyword) {
        return R.ok(formService.pageTemplates(pageQuery, keyword));
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/template/{id}")
    public R<FormTemplate> getTemplate(@PathVariable Long id) {
        return R.ok(formService.getTemplateById(id));
    }

    /**
     * 创建模板
     */
    @PostMapping("/template")
    public R<FormTemplate> createTemplate(@RequestBody FormTemplate template) {
        return R.ok(formService.createTemplate(template));
    }

    /**
     * 更新模板
     */
    @PutMapping("/template")
    public R<Void> updateTemplate(@RequestBody FormTemplate template) {
        formService.updateTemplate(template);
        return R.ok();
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/template/{id}")
    public R<Void> deleteTemplate(@PathVariable Long id) {
        formService.deleteTemplate(id);
        return R.ok();
    }

    // ==================== 字段管理 ====================

    /**
     * 查询模板下所有字段
     */
    @GetMapping("/template/{templateId}/fields")
    public R<List<FormField>> listFields(@PathVariable Long templateId) {
        return R.ok(formService.listFieldsByTemplateId(templateId));
    }

    /**
     * 批量保存字段（覆盖式）
     */
    @PostMapping("/template/{templateId}/fields/batch")
    public R<Void> saveFields(@PathVariable Long templateId,
                               @RequestBody List<FormField> fields) {
        formService.saveFields(templateId, fields);
        return R.ok();
    }

    /**
     * 添加单个字段
     */
    @PostMapping("/field")
    public R<FormField> addField(@RequestBody FormField field) {
        return R.ok(formService.addField(field));
    }

    /**
     * 更新字段
     */
    @PutMapping("/field")
    public R<Void> updateField(@RequestBody FormField field) {
        formService.updateField(field);
        return R.ok();
    }

    /**
     * 删除字段
     */
    @DeleteMapping("/field/{id}")
    public R<Void> deleteField(@PathVariable Long id) {
        formService.deleteField(id);
        return R.ok();
    }

    // ==================== 渲染 & 数据 ====================

    /**
     * 渲染表单（返回前端 JSON Schema）
     */
    @GetMapping("/render/{templateId}")
    public R<Map<String, Object>> renderForm(@PathVariable Long templateId) {
        return R.ok(formService.renderForm(templateId));
    }

    /**
     * 提交表单数据
     */
    @PostMapping("/data/submit")
    public R<FormData> submitFormData(@RequestParam Long templateId,
                                      @RequestParam(required = false) Long eventId,
                                      @RequestParam(required = false) Long userId,
                                      @RequestBody Map<String, Object> formValues) {
        return R.ok(formService.submitFormData(templateId, eventId, userId, formValues));
    }

    /**
     * 查询表单提交数据
     */
    @GetMapping("/data/page")
    public R<PageResult<FormData>> pageFormData(PageQuery pageQuery,
                                                 @RequestParam(required = false) Long templateId,
                                                 @RequestParam(required = false) Long eventId) {
        return R.ok(formService.pageFormData(pageQuery, templateId, eventId));
    }
}
