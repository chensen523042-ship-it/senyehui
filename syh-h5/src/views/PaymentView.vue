<template>
  <div class="page-container">
    <van-nav-bar title="订单支付" left-arrow @click-left="$router.back()" />

    <van-skeleton :loading="!order" :row="5" style="padding: 16px">
      <template #default>
        <!-- 支付状态 -->
        <div class="pay-status" :class="{ 'pay-status--success': order.status === 1 }">
          <van-icon :name="order.status === 1 ? 'passed' : 'clock-o'" size="48" />
          <h2>{{ statusTitle }}</h2>
          <p v-if="order.status === 0">请在 {{ remainTime }} 内完成支付</p>
          <p v-else-if="order.status === 1">支付时间: {{ order.payTime }}</p>
        </div>

        <!-- 订单信息 -->
        <van-cell-group inset class="pay-info">
          <van-cell title="订单编号" :value="order.orderNo" />
          <van-cell title="支付金额">
            <template #value>
              <span class="pay-amount">¥{{ order.amount }}</span>
            </template>
          </van-cell>
          <van-cell title="支付渠道" :value="channelText" />
          <van-cell title="创建时间" :value="order.createTime" />
          <van-cell v-if="order.transactionId" title="交易号" :value="order.transactionId" />
        </van-cell-group>

        <!-- 支付操作 -->
        <div v-if="order.status === 0" class="pay-actions">
          <van-button type="primary" block round size="large" :loading="paying" @click="doMockPay">
            模拟支付 · ¥{{ order.amount }}
          </van-button>
          <p class="pay-tip">
            <van-icon name="info-o" /> 开发模式：点击即模拟支付成功
          </p>
        </div>

        <!-- 已支付 -->
        <div v-if="order.status === 1" class="pay-actions">
          <van-button type="success" block round size="large" @click="$router.push('/my')">
            查看我的报名
          </van-button>
        </div>
      </template>
    </van-skeleton>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createPayOrder, mockPay, getPayStatus } from '@/api'
import { showSuccessToast, showFailToast } from 'vant'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const paying = ref(false)
const countdown = ref(0)
let timer = null

const statusTitle = computed(() => {
  if (!order.value) return ''
  return { 0: '待支付', 1: '支付成功', 2: '订单已取消', 3: '已退款' }[order.value.status] || '未知'
})

const channelText = computed(() => {
  if (!order.value) return ''
  return { mock: '模拟支付', wxpay: '微信支付', alipay: '支付宝' }[order.value.payChannel] || '未知'
})

const remainTime = computed(() => {
  const m = Math.floor(countdown.value / 60)
  const s = countdown.value % 60
  return `${m}分${s.toString().padStart(2, '0')}秒`
})

onMounted(async () => {
  const registrationId = route.params.id
  try {
    const res = await createPayOrder(registrationId)
    order.value = res.data
    if (order.value.status === 0) {
      startCountdown()
    }
  } catch (e) {
    showFailToast(e.message || '创建订单失败')
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function startCountdown() {
  if (!order.value?.expireTime) return
  const update = () => {
    const expire = new Date(order.value.expireTime.replace(/-/g, '/')).getTime()
    const diff = Math.max(0, Math.floor((expire - Date.now()) / 1000))
    countdown.value = diff
    if (diff <= 0 && timer) {
      clearInterval(timer)
      showFailToast('订单已过期')
    }
  }
  update()
  timer = setInterval(update, 1000)
}

async function doMockPay() {
  paying.value = true
  try {
    const res = await mockPay(order.value.orderNo)
    order.value = res.data
    if (timer) clearInterval(timer)
    showSuccessToast('支付成功')
    setTimeout(() => router.push('/my'), 1500)
  } catch (e) {
    showFailToast(e.message || '支付失败')
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
.pay-status {
  text-align: center;
  padding: 40px 16px 24px;
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  border-radius: 0 0 24px 24px;
}
.pay-status--success {
  background: linear-gradient(135deg, #d4fc79, #96e6a1);
}
.pay-status h2 {
  font-size: 22px;
  font-weight: 700;
  margin: 12px 0 4px;
}
.pay-status p {
  font-size: 13px;
  color: var(--syh-text-secondary);
}
.pay-info {
  margin: 16px;
}
.pay-amount {
  font-size: 20px;
  font-weight: 700;
  color: #FF4D4F;
}
.pay-actions {
  padding: 24px 16px;
}
.pay-tip {
  text-align: center;
  margin-top: 12px;
  font-size: 12px;
  color: var(--syh-text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
</style>
