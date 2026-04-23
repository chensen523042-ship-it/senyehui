import request from '@/utils/request'

// 登录
export function login(data) {
  return request.post('/api/auth/login', data)
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/api/auth/userinfo')
}

// 刷新 Token
export function refreshToken(data) {
  return request.post('/api/auth/refresh', data)
}
