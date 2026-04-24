import request from './index'

// 会员管理API

// 查询用户会员信息
export function getUserMembership(userId) {
  return request.get(`/api/membership/user/${userId}`)
}

// 购买会员
export function purchaseMembership(data) {
  return request.post('/api/membership/purchase', data)
}

// 续费会员
export function renewMembership(membershipId) {
  return request.post(`/api/membership/renew/${membershipId}`)
}

// 升级会员
export function upgradeMembership(membershipId, data) {
  return request.post(`/api/membership/upgrade/${membershipId}`, data)
}

// 取消会员
export function cancelMembership(membershipId) {
  return request.post(`/api/membership/cancel/${membershipId}`)
}

// 检查会员有效性
export function checkMembership(userId) {
  return request.get(`/api/membership/check/${userId}`)
}

// 查询会员配置列表
export function getMembershipConfigList() {
  return request.get('/api/membership/config/list')
}

// 查询会员权益列表
export function getMembershipBenefits(membershipId) {
  return request.get(`/api/membership/benefit/list/${membershipId}`)
}

// 检查权益可用性
export function checkBenefit(params) {
  return request.get('/api/membership/benefit/check', { params })
}
