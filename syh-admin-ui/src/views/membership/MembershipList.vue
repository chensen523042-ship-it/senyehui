<template>
  <div class="page-container">
    <div class="card">
      <!-- 搜索条件 -->
      <div class="membership-list__toolbar">
        <div class="membership-list__search">
          <el-input v-model="searchUserId" placeholder="用户ID" clearable style="width: 200px" :prefix-icon="Search" @keyup.enter="loadData" />
          <el-select v-model="searchLevel" placeholder="会员等级" clearable style="width: 140px">
            <el-option label="免费会员" value="FREE" />
            <el-option label="铜牌会员" value="BRONZE" />
            <el-option label="银牌会员" value="SILVER" />
            <el-option label="金牌会员" value="GOLD" />
            <el-option label="白金会员" value="PLATINUM" />
          </el-select>
          <el-select v-model="searchStatus" placeholder="会员状态" clearable style="width: 140px">
            <el-option label="激活" value="ACTIVE" />
            <el-option label="过期" value="EXPIRED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="membershipType" label="会员类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.membershipType === 'INDIVIDUAL' ? 'primary' : 'success'">
              {{ row.membershipType === 'INDIVIDUAL' ? '个人' : '企业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="会员等级" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="levelTagType(row.level)">{{ levelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="到期日期" width="110" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="autoRenew" label="自动续费" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.autoRenew ? 'success' : 'info'" size="small">
              {{ row.autoRenew ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'ACTIVE'" link type="success" size="small" @click="handleRenew(row)">续费</el-button>
            <el-button v-if="row.status === 'ACTIVE'" link type="warning" size="small" @click="handleUpgrade(row)">升级</el-button>
            <el-button v-if="row.status === 'ACTIVE'" link type="danger" size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="membership-list__pagination">
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

    <!-- 会员详情对话框 -->
    <el-dialog v-model="detailVisible" title="会员详情" width="600px">
      <el-descriptions :column="2" border v-if="currentMembership">
        <el-descriptions-item label="用户ID">{{ currentMembership.userId }}</el-descriptions-item>
        <el-descriptions-item label="会员类型">
          {{ currentMembership.membershipType === 'INDIVIDUAL' ? '个人会员' : '企业会员' }}
        </el-descriptions-item>
        <el-descriptions-item label="会员等级">
          <el-tag :type="levelTagType(currentMembership.level)">{{ levelText(currentMembership.level) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentMembership.status)">{{ statusText(currentMembership.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ currentMembership.startDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ currentMembership.endDate }}</el-descriptions-item>
        <el-descriptions-item label="自动续费">
          {{ currentMembership.autoRenew ? '是' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentMembership.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 升级会员对话框 -->
    <el-dialog v-model="upgradeVisible" title="升级会员" width="500px">
      <el-form :model="upgradeForm" label-width="100px">
        <el-form-item label="当前等级">
          <el-tag :type="levelTagType(currentMembership?.level)">{{ levelText(currentMembership?.level) }}</el-tag>
        </el-form-item>
        <el-form-item label="升级到" required>
          <el-select v-model="upgradeForm.targetLevel" placeholder="请选择目标等级" style="width: 100%">
            <el-option v-for="level in availableUpgradeLevels" :key="level.value" :label="level.label" :value="level.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="upgradeVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpgrade">确认升级</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMembershipPage, renewMembership, upgradeMembership, cancelMembership } from '@/api/membership'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchUserId = ref('')
const searchLevel = ref(null)
const searchStatus = ref(null)

const detailVisible = ref(false)
const upgradeVisible = ref(false)
const currentMembership = ref(null)
const upgradeForm = ref({
  targetLevel: ''
})

const levelOrder = ['FREE', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM']

const availableUpgradeLevels = computed(() => {
  if (!currentMembership.value) return []
  const currentIndex = levelOrder.indexOf(currentMembership.value.level)
  return levelOrder.slice(currentIndex + 1).map(level => ({
    value: level,
    label: levelText(level)
  }))
})

const levelText = (level) => {
  const map = {
    FREE: '免费会员',
    BRONZE: '铜牌会员',
    SILVER: '银牌会员',
    GOLD: '金牌会员',
    PLATINUM: '白金会员'
  }
  return map[level] || level
}

const levelTagType = (level) => {
  const map = {
    FREE: 'info',
    BRONZE: 'warning',
    SILVER: '',
    GOLD: 'warning',
    PLATINUM: 'danger'
  }
  return map[level] || ''
}

const statusText = (status) => {
  const map = {
    ACTIVE: '激活',
    EXPIRED: '过期',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const statusTagType = (status) => {
  const map = {
    ACTIVE: 'success',
    EXPIRED: 'warning',
    CANCELLED: 'info'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchUserId.value) params.userId = searchUserId.value
    if (searchLevel.value) params.level = searchLevel.value
    if (searchStatus.value) params.status = searchStatus.value

    const res = await getMembershipPage(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchUserId.value = ''
  searchLevel.value = null
  searchStatus.value = null
  pageNum.value = 1
  loadData()
}

const viewDetail = (row) => {
  currentMembership.value = row
  detailVisible.value = true
}

const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm('确认续费该会员？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await renewMembership(row.id)
    ElMessage.success('续费成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('续费失败')
    }
  }
}

const handleUpgrade = (row) => {
  currentMembership.value = row
  upgradeForm.value.targetLevel = ''
  upgradeVisible.value = true
}

const confirmUpgrade = async () => {
  if (!upgradeForm.value.targetLevel) {
    ElMessage.warning('请选择目标等级')
    return
  }
  try {
    await upgradeMembership(currentMembership.value.id, {
      targetLevel: upgradeForm.value.targetLevel
    })
    ElMessage.success('升级成功')
    upgradeVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('升级失败')
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消该会员？取消后将无法恢复。', '警告', {
      confirmButtonText: '确认取消',
      cancelButtonText: '我再想想',
      type: 'warning'
    })
    await cancelMembership(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.membership-list__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.membership-list__search {
  display: flex;
  gap: 12px;
}

.membership-list__pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
