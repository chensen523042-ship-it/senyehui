import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { setToken, setRefreshToken, clearAuth, getToken } from '@/utils/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)
  const permissions = ref([])

  // 登录
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.accessToken
    setToken(res.data.accessToken)
    setRefreshToken(res.data.refreshToken)
    return res
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
      permissions.value = res.data.permissions || []
      return res.data
    } catch {
      // 获取失败则清除登录状态
      logout()
      return null
    }
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    clearAuth()
    router.push('/login')
  }

  // 是否已登录
  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, permissions, login, fetchUserInfo, logout, isLoggedIn }
})
