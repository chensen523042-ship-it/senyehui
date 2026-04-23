import request from '@/utils/request'

// 获取 Dashboard 统计数据
export function getDashboardStats() {
  return request.get('/api/dashboard/stats')
}

// 获取报名趋势（近7天）
export function getRegistrationTrend() {
  return request.get('/api/dashboard/registration-trend')
}

// 获取赛事状态分布
export function getEventStatusDistribution() {
  return request.get('/api/dashboard/event-status')
}
