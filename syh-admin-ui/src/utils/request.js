import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, getRefreshToken, setToken, setRefreshToken, clearAuth } from './auth'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器 — 自动注入 JWT Token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 是否正在刷新 Token
let isRefreshing = false
let pendingRequests = []

// 响应拦截器 — 统一错误处理 + Token 自动刷新
request.interceptors.response.use(
  response => {
    const res = response.data
    // 业务成功
    if (res.code === 200) {
      return res
    }
    // 业务失败
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  async error => {
    const { response } = error
    if (!response) {
      ElMessage.error('网络异常，请检查连接')
      return Promise.reject(error)
    }

    // 401 — Token 过期，尝试刷新
    if (response.status === 401) {
      const refreshToken = getRefreshToken()
      if (!refreshToken) {
        handleLogout()
        return Promise.reject(error)
      }

      if (!isRefreshing) {
        isRefreshing = true
        try {
          const res = await axios.post('/api/auth/refresh', null, { params: { refreshToken } })
          if (res.data.code === 200) {
            setToken(res.data.data.accessToken)
            setRefreshToken(res.data.data.refreshToken)
            // 重放等待中的请求
            pendingRequests.forEach(cb => cb(res.data.data.accessToken))
            pendingRequests = []
            // 重试原请求
            error.config.headers['Authorization'] = `Bearer ${res.data.data.accessToken}`
            return request(error.config)
          }
        } catch {
          handleLogout()
          return Promise.reject(error)
        } finally {
          isRefreshing = false
        }
      } else {
        // 等待刷新完成
        return new Promise(resolve => {
          pendingRequests.push(token => {
            error.config.headers['Authorization'] = `Bearer ${token}`
            resolve(request(error.config))
          })
        })
      }
    }

    // 403
    if (response.status === 403) {
      ElMessage.error('没有访问权限')
    } else if (response.status === 500) {
      ElMessage.error('服务器内部错误')
    } else {
      ElMessage.error(response.data?.msg || `请求错误 (${response.status})`)
    }

    return Promise.reject(error)
  }
)

function handleLogout() {
  clearAuth()
  ElMessage.warning('登录已过期，请重新登录')
  router.push('/login')
}

export default request
