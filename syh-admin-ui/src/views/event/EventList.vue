<template>
  <div class="page-container">
    <div class="card">
      <!-- 搜索条件 -->
      <div class="event-list__toolbar">
        <div class="event-list__search">
          <el-input v-model="searchTitle" placeholder="搜索赛事名称" clearable style="width: 260px" :prefix-icon="Search" @keyup.enter="loadData" />
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
        <el-button v-permission="'event:create'" type="primary" :icon="Plus" @click="$router.push('/event/create')">创建赛事</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="赛事名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="eventType" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="typeTagType(row.eventType)">{{ typeText(row.eventType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column label="报名" width="120" align="center">
          <template #default="{ row }">
            <span class="event-list__count">{{ row.currentParticipants || 0 }}</span>
            <span v-if="row.maxParticipants > 0" class="event-list__max"> / {{ row.maxParticipants }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/event/edit/${row.id}`)">编辑</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="handleOpenReg(row)">开放报名</el-button>
            <el-button v-if="row.status <= 3" link type="danger" size="small" @click="handleCancel(row)">取消</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
import { getEventPage, publishEvent, openRegistration, cancelEvent, deleteEvent } from '@/api/event'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchTitle = ref('')
const searchStatus = ref(null)

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '待审核', value: 1 },
  { label: '已发布', value: 2 },
  { label: '报名中', value: 3 },
  { label: '进行中', value: 4 },
  { label: '已结束', value: 5 },
  { label: '已取消', value: 6 }
]

function statusTagType(status) {
  return { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: '', 5: 'info', 6: 'danger' }[status] || 'info'
}
function statusText(status) {
  return { 0: '草稿', 1: '待审核', 2: '已发布', 3: '报名中', 4: '进行中', 5: '已结束', 6: '已取消' }[status] || '未知'
}
function typeTagType(type) {
  return { 1: '', 2: 'success', 3: 'warning', 4: 'danger' }[type] || 'info'
}
function typeText(type) {
  return { 1: '赛事', 2: '活动', 3: '培训', 4: '露营' }[type] || '-'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEventPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchTitle.value || undefined,
      status: searchStatus.value ?? undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchTitle.value = ''
  searchStatus.value = null
  pageNum.value = 1
  loadData()
}

async function handlePublish(row) {
  await ElMessageBox.confirm(`确定发布「${row.title}」吗？`, '确认')
  await publishEvent(row.id)
  ElMessage.success('发布成功')
  loadData()
}

async function handleOpenReg(row) {
  await ElMessageBox.confirm(`确定开放「${row.title}」的报名吗？`, '确认')
  await openRegistration(row.id)
  ElMessage.success('操作成功')
  loadData()
}

async function handleCancel(row) {
  await ElMessageBox.confirm(`确定取消「${row.title}」吗？此操作不可逆！`, '警告', { type: 'warning' })
  await cancelEvent(row.id)
  ElMessage.success('已取消')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除「${row.title}」吗？`, '警告', { type: 'warning' })
  await deleteEvent(row.id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.event-list__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.event-list__search {
  display: flex;
  gap: 12px;
}
.event-list__count {
  font-weight: 600;
  color: var(--syh-primary);
}
.event-list__max {
  color: var(--syh-text-secondary);
}
.event-list__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
