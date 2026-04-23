import request from '@/utils/request'

// 表单模板列表
export function getTemplatePage(params) {
  return request.get('/api/form/template/page', { params })
}

// 创建模板
export function createTemplate(data) {
  return request.post('/api/form/template', data)
}

// 更新模板
export function updateTemplate(id, data) {
  return request.put(`/api/form/template/${id}`, data)
}

// 删除模板
export function deleteTemplate(id) {
  return request.delete(`/api/form/template/${id}`)
}

// 获取字段列表
export function getFieldList(templateId) {
  return request.get(`/api/form/field/list/${templateId}`)
}

// 添加字段
export function addField(data) {
  return request.post('/api/form/field', data)
}

// 更新字段
export function updateField(id, data) {
  return request.put(`/api/form/field/${id}`, data)
}

// 删除字段
export function deleteField(id) {
  return request.delete(`/api/form/field/${id}`)
}

// 渲染表单
export function renderForm(templateId) {
  return request.get(`/api/form/render/${templateId}`)
}
