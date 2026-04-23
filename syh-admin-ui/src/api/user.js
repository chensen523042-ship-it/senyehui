import request from '@/utils/request'

// 用户分页列表
export function getUserPage(params) {
  return request.get('/api/system/user/page', { params })
}

// 用户详情（含角色 ID）
export function getUser(id) {
  return request.get(`/api/system/user/${id}`)
}

// 创建用户
export function createUser(data) {
  return request.post('/api/system/user', data)
}

// 更新用户
export function updateUser(data) {
  return request.put('/api/system/user', data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/api/system/user/${id}`)
}

// 启用/禁用用户
export function updateUserStatus(id, status) {
  return request.put(`/api/system/user/${id}/status`, null, { params: { status } })
}

// 重置密码
export function resetUserPassword(id) {
  return request.put(`/api/system/user/${id}/reset-password`)
}
