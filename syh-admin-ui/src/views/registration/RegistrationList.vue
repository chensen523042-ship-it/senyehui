<template>
  <div class="page-container">
    <div class="card">
      <div class="event-list__toolbar">
        <div class="event-list__search">
          <el-select v-model="searchEventId" placeholder="选择赛事" clearable filterable style="width: 260px" @change="loadData">
            <el-option v-for="e in eventOptions" :key="e.id" :label="e.title" :value="e.id" />
          </el-select>
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
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
        <el-table-column prop="payStatus" label="支付" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="payTagType(row.payStatus)" size="small">{{ payText(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInStatus" label="签到" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.checkInStatus === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
            <el-button v-if="row.status === 1 && row.checkInStatus === 0" link type="primary" size="small" @click="handleCheckIn(row)">签到</el-button>
            <el-button v-if="row.payStatus === 1 && row.payOrderNo" link type="warning" size="small" @click="handleRefund(row)">退款</el-button>
            <el-button v-if="row.status !== 3" link type="info" size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="event-list__pagination">
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
import { getRegistrationPage, auditRegistration, checkIn, cancelRegistration, refundOrder } from '@/api/registration'
import { getEventPage } from '@/api/event'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchEventId = ref(null)
const searchStatus = ref(null)
const eventOptions = ref([])

function statusTagType(s) { return { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[s] || 'info' }
function statusText(s) { return { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }[s] || '-' }
function payTagType(s) { return { 0: 'warning', 1: 'success', 2: 'info' }[s] || 'info' }
function payText(s) { return { 0: '未支付', 1: '已支付', 2: '已退款' }[s] || '-' }

async function loadData() {
  loading.value = true
  try {
    const res = await getRegistrationPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      eventId: searchEventId.value || undefined,
      status: searchStatus.value ?? undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

async function handleAudit(row, status) {
  const action = status === 1 ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确定${action}「${row.name}」的报名吗？`, '确认')
  await auditRegistration(row.id, status)
  ElMessage.success(`已${action}`)
  loadData()
}

async function handleCheckIn(row) {
  await checkIn(row.id)
  ElMessage.success('签到成功')
  loadData()
}

async function handleRefund(row) {
  await ElMessageBox.confirm(`确定对「${row.name}」的订单进行退款吗？金额：¥${row.amount}`, '退款确认', { type: 'warning' })
  await refundOrder(row.payOrderNo)
  ElMessage.success('退款成功')
  loadData()
}

async function handleCancel(row) {
  await ElMessageBox.confirm(`确定取消「${row.name}」的报名吗？`, '警告', { type: 'warning' })
  await cancelRegistration(row.id)
  ElMessage.success('已取消')
  loadData()
}

onMounted(async () => {
  // 加载赛事选项
  try {
    const res = await getEventPage({ pageNum: 1, pageSize: 100 })
    eventOptions.value = res.data?.records || []
  } catch { /* ignore */ }
  loadData()
})
</script>

<style scoped>
.event-list__toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.event-list__search {
  display: flex;
  gap: 12px;
}
.event-list__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
