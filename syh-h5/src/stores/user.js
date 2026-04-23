import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('syh_h5_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('syh_h5_userinfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  function setLoginData(data) {
    token.value = data.accessToken
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      avatar: data.avatar
    }
    localStorage.setItem('syh_h5_token', data.accessToken)
    localStorage.setItem('syh_h5_userinfo', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('syh_h5_token')
    localStorage.removeItem('syh_h5_userinfo')
  }

  return { token, userInfo, isLoggedIn, setLoginData, logout }
})
