import request from '@/utils/request'

export function getCategoryList() {
  return request.get('/api/event/category/list')
}

export function createCategory(data) {
  return request.post('/api/event/category', data)
}

export function updateCategory(data) {
  return request.put('/api/event/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/api/event/category/${id}`)
}
