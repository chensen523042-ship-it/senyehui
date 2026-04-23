import request from '@/utils/request'

export function getPaymentPage(params) {
  return request.get('/api/registration/page', { params })
}

export function refundOrder(orderNo) {
  return request.post('/api/pay/refund', null, { params: { orderNo } })
}

export function getPayOrder(orderNo) {
  return request.get(`/api/pay/order/${orderNo}`)
}

export function getTotalIncome(tenantId) {
  return request.get('/api/pay/income', { params: { tenantId } })
}
