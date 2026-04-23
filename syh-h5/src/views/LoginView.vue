<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-content">
      <!-- Logo -->
      <div class="login-logo">
        <div class="login-logo__icon">🏔️</div>
        <h1>森野汇</h1>
        <p>户外赛事报名平台</p>
      </div>

      <!-- 登录表单 -->
      <div class="login-card">
        <van-tabs v-model:active="loginType" animated>
          <!-- 手机号模拟登录 -->
          <van-tab title="手机号登录">
            <van-form @submit="onPhoneLogin" class="login-form">
              <van-cell-group inset>
                <van-field
                  v-model="phoneForm.phone"
                  type="tel"
                  label="手机号"
                  placeholder="请输入手机号"
                  :rules="[{ required: true, message: '请输入手机号' }]"
                />
                <van-field
                  v-model="phoneForm.code"
                  center
                  label="验证码"
                  placeholder="请输入验证码"
                  :rules="[{ required: true, message: '请输入验证码' }]"
                >
                  <template #button>
                    <van-button
                      size="small"
                      type="primary"
                      :disabled="countdown > 0"
                      @click.prevent="sendCode"
                    >
                      {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                    </van-button>
                  </template>
                </van-field>
              </van-cell-group>
              <div class="login-submit">
                <van-button type="primary" block round size="large" native-type="submit" :loading="loading">
                  登录
                </van-button>
              </div>
            </van-form>
          </van-tab>

          <!-- 账号密码登录（复用管理端） -->
          <van-tab title="账号登录">
            <van-form @submit="onAccountLogin" class="login-form">
              <van-cell-group inset>
                <van-field
                  v-model="accountForm.username"
                  label="账号"
                  placeholder="请输入账号"
                  :rules="[{ required: true, message: '请输入账号' }]"
                />
                <van-field
                  v-model="accountForm.password"
                  type="password"
                  label="密码"
                  placeholder="请输入密码"
                  :rules="[{ required: true, message: '请输入密码' }]"
                />
              </van-cell-group>
              <div class="login-submit">
                <van-button type="primary" block round size="large" native-type="submit" :loading="loading">
                  登录
                </van-button>
              </div>
            </van-form>
          </van-tab>
        </van-tabs>

        <div class="login-tip">
          <van-icon name="info-o" /> 测试账号: admin / admin123
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { h5Login } from '@/api'
import { showSuccessToast, showFailToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const loginType = ref(1) // 默认显示账号登录 tab
const countdown = ref(0)

const phoneForm = ref({ phone: '', code: '' })
const accountForm = ref({ username: 'admin', password: 'admin123' })

// 模拟发送验证码
function sendCode() {
  if (!phoneForm.value.phone) {
    showFailToast('请输入手机号')
    return
  }
  countdown.value = 60
  showSuccessToast('验证码已发送: 1234')
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

// 手机号模拟登录（使用固定验证码 1234）
async function onPhoneLogin() {
  if (phoneForm.value.code !== '1234') {
    showFailToast('验证码错误（请输入 1234）')
    return
  }
  // 模拟 → 用管理端 admin 账号登录
  await doLogin({ username: 'admin', password: 'admin123' })
}

// 账号密码登录
async function onAccountLogin() {
  await doLogin(accountForm.value)
}

async function doLogin(credentials) {
  loading.value = true
  try {
    const res = await h5Login(credentials)
    userStore.setLoginData(res.data)
    showSuccessToast('登录成功')
    const redirect = route.query.redirect || '/'
    setTimeout(() => router.replace(redirect), 800)
  } catch (e) {
    showFailToast(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
}
.login-bg {
  position: absolute;
  inset: 0;
  background: var(--syh-gradient);
  clip-path: ellipse(160% 65% at 50% 0%);
}
.login-content {
  position: relative;
  z-index: 1;
  padding-top: 60px;
}
.login-logo {
  text-align: center;
  margin-bottom: 30px;
}
.login-logo__icon {
  font-size: 52px;
  margin-bottom: 8px;
}
.login-logo h1 {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 4px;
}
.login-logo p {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
}
.login-card {
  margin: 0 16px;
  background: var(--syh-card);
  border-radius: 20px;
  padding: 20px 0 24px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}
.login-form {
  padding-top: 16px;
}
.login-submit {
  padding: 24px 16px 0;
}
.login-tip {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: var(--syh-text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
</style>
