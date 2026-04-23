<template>
  <div class="page-container">
    <div class="card">
      <div class="checkin__toolbar">
        <div class="checkin__search">
          <el-select v-model="searchEventId" placeholder="选择赛事" clearable filterable style="width: 260px" @change="loadData">
            <el-option v-for="e in eventOptions" :key="e.id" :label="e.title" :value="e.id" />
          </el-select>
          <el-select v-model="searchCheckInStatus" placeholder="签到状态" clearable style="width: 140px" @change="loadData">
            <el-option label="未签到" :value="0" />
            <el-option label="已签到" :value="1" />
          </el-select>
          <el-input v-model="searchKeyword" placeholder="姓名 / 手机号" clearable style="width: 200px" @keyup.enter="loadData" />
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
        <div class="checkin__stats" v-if="tableData.length > 0 || total > 0">
          <el-tag type="info">总计 {{ total }}</el-tag>
          <el-tag type="success">已签到 {{ checkedInCount }}</el-tag>
          <el-tag type="warning">未签到 {{ total - checkedInCount }}</el-tag>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="regNo" label="报名编号" width="200" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="checkInStatus" label="签到状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.checkInStatus === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="签到时间" width="170" />
        <el-table-column prop="createTime" label="报名时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.checkInStatus === 0 && row.status === 1" link type="primary" size="small" @click="handleCheckIn(row)">签到</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="checkin__pagination">
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
import { ref, computed, onMounted } from 'vue'
import { getRegistrationPage, checkIn } from '@/api/registration'
import { getEventPage } from '@/api/event'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchEventId = ref(null)
const searchCheckInStatus = ref(null)
const searchKeyword = ref('')
const eventOptions = ref([])

const checkedInCount = computed(() => tableData.value.filter(r => r.checkInStatus === 1).length)

async function loadData() {
  loading.value = true
  try {
    const res = await getRegistrationPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      eventId: searchEventId.value || undefined,
      checkInStatus: searchCheckInStatus.value ?? undefined,
      keyword: searchKeyword.value || undefined,
      status: 1  // 只显示已通过的报名
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchEventId.value = null
  searchCheckInStatus.value = null
  searchKeyword.value = ''
  pageNum.value = 1
  loadData()
}

async function handleCheckIn(row) {
  await checkIn(row.id)
  ElMessage.success(`「${row.name}」签到成功`)
  loadData()
}

onMounted(async () => {
  try {
    const res = await getEventPage({ pageNum: 1, pageSize: 100 })
    eventOptions.value = res.data?.records || []
  } catch { /* ignore */ }
  loadData()
})
</script>

<style scoped>
.checkin__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.checkin__search {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.checkin__stats {
  display: flex;
  gap: 8px;
}
.checkin__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
