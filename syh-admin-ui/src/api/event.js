import request from '@/utils/request'

// 赛事列表
export function getEventPage(params) {
  return request.get('/api/event/page', { params })
}

// 赛事详情
export function getEvent(id) {
  return request.get(`/api/event/${id}`)
}

// 创建赛事
export function createEvent(data) {
  return request.post('/api/event', data)
}

// 更新赛事
export function updateEvent(id, data) {
  data.id = id
  return request.put('/api/event', data)
}

// 删除赛事
export function deleteEvent(id) {
  return request.delete(`/api/event/${id}`)
}

// 发布赛事
export function publishEvent(id) {
  return request.put(`/api/event/${id}/publish`)
}

// 开启报名
export function openRegistration(id) {
  return request.put(`/api/event/${id}/open-registration`)
}

// 下架赛事
export function unpublishEvent(id) {
  return request.put(`/api/event/${id}/unpublish`)
}

// 取消赛事
export function cancelEvent(id) {
  return request.put(`/api/event/${id}/cancel`)
}

// 分类管理
export function getCategoryList() {
  return request.get('/api/event/category/list')
}

export function createCategory(data) {
  return request.post('/api/event/category', data)
}

export function updateCategory(id, data) {
  data.id = id
  return request.put('/api/event/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/api/event/category/${id}`)
}

// 组别管理
export function getGroupList(eventId) {
  return request.get(`/api/event/group/list/${eventId}`)
}

export function createGroup(data) {
  return request.post('/api/event/group', data)
}

export function updateGroup(id, data) {
  data.id = id
  return request.put('/api/event/group', data)
}

export function deleteGroup(id) {
  return request.delete(`/api/event/group/${id}`)
}
