import request from '@/utils/request'

// 会员管理API

// 查询会员列表
export function getMembershipPage(params) {
  return request({
    url: '/api/membership/page',
    method: 'get',
    params
  })
}

// 查询用户会员信息
export function getUserMembership(userId) {
  return request({
    url: `/api/membership/user/${userId}`,
    method: 'get'
  })
}

// 购买会员
export function purchaseMembership(data) {
  return request({
    url: '/api/membership/purchase',
    method: 'post',
    data
  })
}

// 续费会员
export function renewMembership(membershipId) {
  return request({
    url: `/api/membership/renew/${membershipId}`,
    method: 'post'
  })
}

// 升级会员
export function upgradeMembership(membershipId, data) {
  return request({
    url: `/api/membership/upgrade/${membershipId}`,
    method: 'post',
    data
  })
}

// 取消会员
export function cancelMembership(membershipId) {
  return request({
    url: `/api/membership/cancel/${membershipId}`,
    method: 'post'
  })
}

// 检查会员有效性
export function checkMembership(userId) {
  return request({
    url: `/api/membership/check/${userId}`,
    method: 'get'
  })
}

// 会员配置API

// 查询会员配置列表
export function getMembershipConfigList() {
  return request({
    url: '/api/membership/config/list',
    method: 'get'
  })
}

// 查询会员配置详情
export function getMembershipConfig(id) {
  return request({
    url: `/api/membership/config/${id}`,
    method: 'get'
  })
}

// 创建会员配置
export function createMembershipConfig(data) {
  return request({
    url: '/api/membership/config',
    method: 'post',
    data
  })
}

// 更新会员配置
export function updateMembershipConfig(id, data) {
  return request({
    url: `/api/membership/config/${id}`,
    method: 'put',
    data
  })
}

// 删除会员配置
export function deleteMembershipConfig(id) {
  return request({
    url: `/api/membership/config/${id}`,
    method: 'delete'
  })
}

// 会员权益API

// 查询会员权益列表
export function getMembershipBenefits(membershipId) {
  return request({
    url: `/api/membership/benefit/list/${membershipId}`,
    method: 'get'
  })
}

// 分配权益
export function allocateBenefit(data) {
  return request({
    url: '/api/membership/benefit/allocate',
    method: 'post',
    data
  })
}

// 消费权益
export function consumeBenefit(benefitId) {
  return request({
    url: `/api/membership/benefit/consume/${benefitId}`,
    method: 'post'
  })
}

// 检查权益可用性
export function checkBenefit(params) {
  return request({
    url: '/api/membership/benefit/check',
    method: 'get',
    params
  })
}
