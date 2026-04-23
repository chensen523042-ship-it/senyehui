<template>
  <div class="page-container">
    <van-nav-bar title="报名" left-arrow @click-left="$router.back()" />

    <van-skeleton :loading="!event" :row="6" style="padding: 16px">
      <template #default>
        <!-- 赛事摘要 -->
        <div class="reg-event-summary">
          <h3>{{ event.title }}</h3>
          <div class="reg-event-meta">
            <van-icon name="clock-o" /> {{ event.startTime || '待定' }}
            <span class="reg-divider">|</span>
            <van-icon name="location-o" /> {{ event.location }}
          </div>
        </div>

        <!-- 报名表单 -->
        <van-form @submit="onSubmit" class="reg-form">
          <van-cell-group inset>
            <van-field
              v-model="form.name"
              label="姓名"
              placeholder="请输入真实姓名"
              :rules="[{ required: true, message: '请输入姓名' }]"
            />
            <van-field
              v-model="form.phone"
              label="手机号"
              type="tel"
              placeholder="请输入手机号"
              :rules="[
                { required: true, message: '请输入手机号' },
                { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误' }
              ]"
            />
            <van-field
              v-model="form.idCard"
              label="身份证"
              placeholder="请输入身份证号"
              :rules="[
                { required: true, message: '请输入身份证号' },
                { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式错误' }
              ]"
            />
            <van-field
              v-if="form.emergencyContact !== undefined"
              v-model="form.emergencyContact"
              label="紧急联系人"
              placeholder="紧急联系人姓名"
            />
            <van-field
              v-if="form.emergencyPhone !== undefined"
              v-model="form.emergencyPhone"
              label="紧急电话"
              type="tel"
              placeholder="紧急联系人电话"
            />
          </van-cell-group>

          <!-- 费用说明 -->
          <div class="reg-price-info">
            <div class="reg-price-row">
              <span>报名费</span>
              <span class="reg-price-value" v-if="event.price > 0">¥{{ event.price }}</span>
              <van-tag v-else type="success">免费</van-tag>
            </div>
          </div>

          <!-- 提交 -->
          <div class="reg-submit">
            <van-button type="primary" block round size="large" native-type="submit" :loading="submitting">
              {{ event.price > 0 ? `确认报名 · ¥${event.price}` : '确认报名（免费）' }}
            </van-button>
          </div>
        </van-form>
      </template>
    </van-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getEventDetail, submitRegistration } from '@/api'
import { showSuccessToast, showFailToast } from 'vant'

const route = useRoute()
const router = useRouter()
const event = ref(null)
const submitting = ref(false)
const form = ref({
  name: '',
  phone: '',
  idCard: '',
  emergencyContact: undefined,
  emergencyPhone: undefined
})

onMounted(async () => {
  try {
    const res = await getEventDetail(route.params.id)
    event.value = res.data
  } catch {
    showFailToast('加载赛事信息失败')
  }
})

async function onSubmit() {
  submitting.value = true
  try {
    const res = await submitRegistration({
      eventId: route.params.id,
      name: form.value.name,
      phone: form.value.phone,
      idCard: form.value.idCard
    })
    const registration = res.data
    // 收费赛事 → 跳转支付页面
    if (event.value.price > 0 && registration.payStatus === 0) {
      showSuccessToast('报名成功，请支付')
      setTimeout(() => router.push(`/pay/${registration.id}`), 1000)
    } else {
      showSuccessToast('报名成功')
      setTimeout(() => router.push('/my'), 1500)
    }
  } catch (e) {
    showFailToast(e.message || '报名失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.reg-event-summary {
  margin: 16px;
  padding: 16px;
  background: var(--syh-card);
  border-radius: 12px;
  border-left: 4px solid var(--syh-primary);
}
.reg-event-summary h3 {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 8px;
}
.reg-event-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--syh-text-secondary);
}
.reg-divider {
  margin: 0 8px;
  color: #ddd;
}
.reg-form {
  margin-top: 8px;
}
.reg-price-info {
  margin: 16px;
  padding: 16px;
  background: var(--syh-card);
  border-radius: 12px;
}
.reg-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
}
.reg-price-value {
  font-size: 22px;
  font-weight: 700;
  color: #FF4D4F;
}
.reg-submit {
  padding: 16px;
}
</style>
