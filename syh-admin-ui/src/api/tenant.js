import request from '@/utils/request'

export function getTenantPage(params) {
  return request.get('/api/tenant/page', { params })
}

export function getTenant(id) {
  return request.get(`/api/tenant/${id}`)
}

export function createTenant(data) {
  return request.post('/api/tenant', data)
}

export function updateTenant(data) {
  return request.put('/api/tenant', data)
}

export function updateTenantStatus(id, status) {
  return request.put(`/api/tenant/${id}/status/${status}`)
}

export function deleteTenant(id) {
  return request.delete(`/api/tenant/${id}`)
}
