<template>
  <div class="page-container">
    <div class="card">
      <!-- 工具栏 -->
      <div class="config-list__toolbar">
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增配置</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="配置名称" min-width="150" />
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
        <el-table-column prop="price" label="价格" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="durationMonths" label="有效期" width="100" align="center">
          <template #default="{ row }">
            {{ row.durationMonths }}个月
          </template>
        </el-table-column>
        <el-table-column prop="discountRate" label="折扣率" width="100" align="center">
          <template #default="{ row }">
            {{ (row.discountRate * 100).toFixed(0) }}%
          </template>
        </el-table-column>
        <el-table-column prop="pointsMultiplier" label="积分倍率" width="100" align="center">
          <template #default="{ row }">
            {{ row.pointsMultiplier }}x
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="handleToggle(row)">
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="会员类型" prop="membershipType">
          <el-select v-model="form.membershipType" placeholder="请选择会员类型" style="width: 100%">
            <el-option label="个人会员" value="INDIVIDUAL" />
            <el-option label="企业会员" value="ENTERPRISE" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择会员等级" style="width: 100%">
            <el-option label="免费会员" value="FREE" />
            <el-option label="铜牌会员" value="BRONZE" />
            <el-option label="银牌会员" value="SILVER" />
            <el-option label="金牌会员" value="GOLD" />
            <el-option label="白金会员" value="PLATINUM" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期(月)" prop="durationMonths">
          <el-input-number v-model="form.durationMonths" :min="1" :max="120" style="width: 100%" />
        </el-form-item>
        <el-form-item label="折扣率" prop="discountRate">
          <el-input-number v-model="form.discountRate" :min="0" :max="1" :step="0.01" :precision="2" style="width: 100%" />
          <span style="margin-left: 8px; color: #909399;">0-1之间，1表示无折扣</span>
        </el-form-item>
        <el-form-item label="积分倍率" prop="pointsMultiplier">
          <el-input-number v-model="form.pointsMultiplier" :min="1" :max="10" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="启用状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMembershipConfigList, createMembershipConfig, updateMembershipConfig, deleteMembershipConfig } from '@/api/membership'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const form = ref({
  id: null,
  name: '',
  membershipType: 'INDIVIDUAL',
  level: 'SILVER',
  price: 0,
  durationMonths: 12,
  discountRate: 1.0,
  pointsMultiplier: 1.0,
  description: '',
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  membershipType: [{ required: true, message: '请选择会员类型', trigger: 'change' }],
  level: [{ required: true, message: '请选择会员等级', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  durationMonths: [{ required: true, message: '请输入有效期', trigger: 'blur' }],
  discountRate: [{ required: true, message: '请输入折扣率', trigger: 'blur' }],
  pointsMultiplier: [{ required: true, message: '请输入积分倍率', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑会员配置' : '新增会员配置')

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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMembershipConfigList()
    tableData.value = res.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    membershipType: 'INDIVIDUAL',
    level: 'SILVER',
    price: 0,
    durationMonths: 12,
    discountRate: 1.0,
    pointsMultiplier: 1.0,
    description: '',
    enabled: true
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateMembershipConfig(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createMembershipConfig(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  }
}

const handleToggle = async (row) => {
  try {
    await updateMembershipConfig(row.id, {
      ...row,
      enabled: !row.enabled
    })
    ElMessage.success(row.enabled ? '禁用成功' : '启用成功')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该配置？删除后无法恢复。', '警告', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMembershipConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.config-list__toolbar {
  margin-bottom: 16px;
}
</style>
