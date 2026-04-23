<template>
  <div class="login-page">
    <!-- 背景动画 -->
    <div class="login-bg">
      <div class="login-bg__circle login-bg__circle--1"></div>
      <div class="login-bg__circle login-bg__circle--2"></div>
      <div class="login-bg__circle login-bg__circle--3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-card__header">
        <div class="login-card__logo">
          <el-icon :size="28"><Trophy /></el-icon>
        </div>
        <h1 class="login-card__title">森野汇</h1>
        <p class="login-card__subtitle">体育赛事与户外活动 SaaS 管理平台</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-card__form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="tenantId">
          <el-input
            v-model.number="loginForm.tenantId"
            placeholder="租户ID (默认: 1)"
            :prefix-icon="OfficeBuilding"
            size="large"
          />
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          class="login-card__btn"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form>

      <div class="login-card__footer">
        <span>© 2026 森野汇 · 赛事管理云平台</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  tenantId: 1
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
      tenantId: loginForm.tenantId
    })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (err) {
    // Axios 拦截器已处理错误提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  position: relative;
  overflow: hidden;
}

/* 背景动画圆形 */
.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.login-bg__circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: float 20s ease-in-out infinite;
}

.login-bg__circle--1 {
  width: 600px;
  height: 600px;
  background: #3b82f6;
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.login-bg__circle--2 {
  width: 400px;
  height: 400px;
  background: #8b5cf6;
  bottom: -100px;
  left: -100px;
  animation-delay: -7s;
}

.login-bg__circle--3 {
  width: 500px;
  height: 500px;
  background: #06b6d4;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -30px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, 15px) scale(1.02); }
}

/* 登录卡片 */
.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  z-index: 1;
  animation: cardIn 0.6s ease-out;
}

@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-card__header {
  text-align: center;
  margin-bottom: 32px;
}

.login-card__logo {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
}

.login-card__title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.login-card__subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

/* 表单 */
.login-card__form {
  margin-bottom: 16px;
}

.login-card__form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  box-shadow: none !important;
  padding: 4px 12px;
}

.login-card__form :deep(.el-input__wrapper:hover) {
  border-color: rgba(59, 130, 246, 0.4);
}

.login-card__form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--syh-primary);
  background: rgba(59, 130, 246, 0.06);
}

.login-card__form :deep(.el-input__inner) {
  color: #ffffff;
}

.login-card__form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.35);
}

.login-card__form :deep(.el-input__prefix .el-icon) {
  color: rgba(255, 255, 255, 0.4);
}

.login-card__btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border: none;
  transition: all 0.3s;
}

.login-card__btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}

.login-card__footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
}
</style>
