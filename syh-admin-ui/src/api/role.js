import request from '@/utils/request'

export function getRoleList() {
  return request.get('/api/system/role/list')
}

export function getRole(id) {
  return request.get(`/api/system/role/${id}`)
}

export function createRole(data) {
  return request.post('/api/system/role', data)
}

export function updateRole(data) {
  return request.put('/api/system/role', data)
}

export function deleteRole(id) {
  return request.delete(`/api/system/role/${id}`)
}

export function assignPermissions(id, permissionIds) {
  return request.put(`/api/system/role/${id}/permissions`, permissionIds)
}
