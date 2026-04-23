import axios from 'axios'

const request = axios.create({
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器 — 自动注入 JWT Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('syh_h5_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('syh_h5_token')
      localStorage.removeItem('syh_h5_userinfo')
      if (window.location.pathname !== '/login') {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`
      }
    }
    return Promise.reject(error)
  }
)

export default request

// ========== 认证 API ==========

export function h5Login(data) {
  return request.post('/api/auth/login', data)
}

// ========== C端公开 API ==========

export function getEventList(params) {
  return request.get('/open/event/list', { params })
}

export function getEventDetail(id) {
  return request.get(`/open/event/${id}`)
}

export function getEventGroups(eventId) {
  return request.get(`/open/event/${eventId}/groups`)
}

// ========== 报名 API ==========

export function submitRegistration(data) {
  return request.post('/open/registration/submit', data)
}

export function getMyRegistrations(params) {
  return request.get('/open/registration/my', { params })
}

export function cancelRegistration(id) {
  return request.put(`/open/registration/${id}/cancel`)
}

// ========== 支付 API ==========

export function createPayOrder(registrationId) {
  return request.post('/open/pay/create', null, { params: { registrationId } })
}

export function mockPay(orderNo) {
  return request.post('/open/pay/mock-pay', null, { params: { orderNo } })
}

export function getPayStatus(orderNo) {
  return request.get(`/open/pay/status/${orderNo}`)
}

export function getPayOrder(registrationId) {
  return request.get(`/open/pay/order/${registrationId}`)
}
