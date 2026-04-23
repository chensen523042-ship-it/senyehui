<template>
  <div class="page-container">
    <van-nav-bar title="我的报名" />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadMore">
        <div class="my-list">
          <div
            v-for="reg in registrations"
            :key="reg.id"
            class="my-card"
          >
            <div class="my-card__header">
              <span class="my-card__no">{{ reg.regNo }}</span>
              <van-tag :type="regStatusType(reg.status)" size="medium">
                {{ regStatusText(reg.status) }}
              </van-tag>
            </div>
            <div class="my-card__body">
              <div class="my-card__row">
                <van-icon name="contact-o" size="16" />
                <span>{{ reg.name }} · {{ reg.phone }}</span>
              </div>
              <div class="my-card__row">
                <van-icon name="clock-o" size="16" />
                <span>报名时间: {{ reg.createTime }}</span>
              </div>
              <div v-if="reg.amount > 0" class="my-card__row">
                <van-icon name="gold-coin-o" size="16" />
                <span>费用: ¥{{ reg.amount }}</span>
                <van-tag :type="payTagType(reg.payStatus)" size="small" style="margin-left: 8px">
                  {{ payStatusText(reg.payStatus) }}
                </van-tag>
              </div>
            </div>
            <div v-if="reg.amount > 0 && reg.payStatus === 0 && reg.status !== 3" class="my-card__actions">
              <van-button type="danger" size="small" round @click="goToPay(reg.id)">
                去支付 · ¥{{ reg.amount }}
              </van-button>
            </div>
          </div>
        </div>

        <van-empty v-if="!loading && registrations.length === 0" description="暂无报名记录" image="default" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyRegistrations } from '@/api'

const router = useRouter()

const registrations = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)

async function loadMore() {
  try {
    const res = await getMyRegistrations({ pageNum: pageNum.value, pageSize: 10 })
    const records = res.data?.records || []
    if (pageNum.value === 1) {
      registrations.value = records
    } else {
      registrations.value.push(...records)
    }
    pageNum.value++
    finished.value = records.length < 10
  } catch {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  pageNum.value = 1
  finished.value = false
  loadMore()
}

function regStatusType(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger', 3: 'default' }[s] || 'default'
}
function regStatusText(s) {
  return { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }[s] || '未知'
}
function payTagType(s) {
  return { 0: 'warning', 1: 'success', 2: 'default' }[s] || 'default'
}
function payStatusText(s) {
  return { 0: '未支付', 1: '已支付', 2: '已退款' }[s] || '未知'
}
function goToPay(regId) {
  router.push(`/pay/${regId}`)
}
</script>

<style scoped>
.my-list {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.my-card {
  background: var(--syh-card);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0,0,0,0.05);
}
.my-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f0f4ff, #fdf2f8);
  border-bottom: 1px solid var(--syh-border);
}
.my-card__no {
  font-weight: 600;
  font-size: 14px;
  color: var(--syh-primary);
  font-family: 'Courier New', monospace;
}
.my-card__body {
  padding: 12px 16px;
}
.my-card__row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--syh-text-secondary);
  margin-bottom: 6px;
}
.my-card__row:last-child {
  margin-bottom: 0;
}
.my-card__actions {
  padding: 0 16px 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
