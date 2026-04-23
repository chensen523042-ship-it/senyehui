<template>
  <div class="page-container">
    <!-- 收入统计卡片 -->
    <div class="card" style="margin-bottom: 16px;">
      <div class="payment-stat">
        <div class="payment-stat__item">
          <div class="payment-stat__label">总收入</div>
          <div class="payment-stat__value">¥{{ totalIncome }}</div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="payment-list__toolbar">
        <div class="payment-list__search">
          <el-select v-model="searchPayStatus" placeholder="支付状态" clearable style="width: 140px" @change="loadData">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="regNo" label="报名编号" width="200" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="amount" label="金额" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.amount > 0" style="color: var(--syh-warning); font-weight: 600;">¥{{ row.amount }}</span>
            <span v-else style="color: var(--syh-success);">免费</span>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="payTagType(row.payStatus)" size="small">{{ payText(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payOrderNo" label="订单号" width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="报名时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.payStatus === 1 && row.payOrderNo" link type="warning" size="small" @click="handleRefund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="payment-list__pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPaymentPage, refundOrder, getTotalIncome } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchPayStatus = ref(null)
const totalIncome = ref('0.00')

function payTagType(s) { return { 0: 'warning', 1: 'success', 2: 'info' }[s] ?? 'info' }
function payText(s) { return { 0: '未支付', 1: '已支付', 2: '已退款' }[s] ?? '-' }

async function loadData() {
  loading.value = true
  try {
    const res = await getPaymentPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      payStatus: searchPayStatus.value ?? undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchPayStatus.value = null
  pageNum.value = 1
  loadData()
}

async function handleRefund(row) {
  await ElMessageBox.confirm(`确定对「${row.name}」的订单退款 ¥${row.amount} 吗？`, '退款确认', { type: 'warning' })
  await refundOrder(row.payOrderNo)
  ElMessage.success('退款成功')
  loadData()
}

onMounted(async () => {
  try {
    const res = await getTotalIncome(1)
    totalIncome.value = res.data ?? '0.00'
  } catch { /* handled */ }
  loadData()
})
</script>

<style scoped>
.payment-stat {
  display: flex;
  gap: 32px;
}
.payment-stat__label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}
.payment-stat__value {
  font-size: 28px;
  font-weight: 700;
  color: var(--syh-warning, #e6a23c);
}
.payment-list__toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.payment-list__search {
  display: flex;
  gap: 12px;
}
.payment-list__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
