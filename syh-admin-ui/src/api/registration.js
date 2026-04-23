import request from '@/utils/request'

// 报名列表
export function getRegistrationPage(params) {
  return request.get('/api/registration/page', { params })
}

// 报名详情
export function getRegistration(id) {
  return request.get(`/api/registration/${id}`)
}

// 审核报名
export function auditRegistration(id, status) {
  return request.put(`/api/registration/${id}/audit/${status}`)
}

// 签到
export function checkIn(id) {
  return request.put(`/api/registration/${id}/check-in`)
}

// 取消报名
export function cancelRegistration(id) {
  return request.put(`/api/registration/${id}/cancel`)
}

// 退款
export function refundOrder(orderNo) {
  return request.post('/api/pay/refund', null, { params: { orderNo } })
}

// 收入统计
export function getIncome(tenantId = 1) {
  return request.get('/api/pay/income', { params: { tenantId } })
}
