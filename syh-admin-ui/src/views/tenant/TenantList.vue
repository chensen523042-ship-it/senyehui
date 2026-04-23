<template>
  <div class="page-container">
    <div class="card">
      <div class="tenant-list__toolbar">
        <div class="tenant-list__search">
          <el-input v-model="searchKeyword" placeholder="租户名称 / 编码 / 联系人" clearable style="width: 260px" :prefix-icon="Search" @keyup.enter="loadData" />
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="正常" :value="1" />
            <el-option label="试用" :value="2" />
            <el-option label="停用" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增租户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="tenantCode" label="租户编码" width="130" />
        <el-table-column prop="tenantName" label="租户名称" width="180" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="到期时间" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="tenant-list__pagination">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑租户' : '新增租户'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="租户编码" prop="tenantCode">
          <el-input v-model="form.tenantCode" placeholder="唯一标识，如 demo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="form.tenantName" placeholder="组织者名称" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" placeholder="联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form.contactEmail" placeholder="联系邮箱" />
        </el-form-item>
        <el-form-item label="到期时间">
          <el-date-picker v-model="form.expireTime" type="datetime" placeholder="选择到期时间" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="2">试用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getTenantPage, createTenant, updateTenant, updateTenantStatus, deleteTenant } from '@/api/tenant'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchStatus = ref(null)

function statusTagType(s) { return { 1: 'success', 2: 'warning', 0: 'danger' }[s] ?? 'info' }
function statusText(s) { return { 1: '正常', 2: '试用', 0: '停用' }[s] ?? '-' }

async function loadData() {
  loading.value = true
  try {
    const res = await getTenantPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value ?? undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchKeyword.value = ''
  searchStatus.value = null
  pageNum.value = 1
  loadData()
}

// ==================== 弹窗 ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, tenantCode: '', tenantName: '', contactName: '', contactPhone: '', contactEmail: '', expireTime: null, status: 1, remark: '' })

const formRules = {
  tenantCode: [{ required: true, message: '请输入租户编码', trigger: 'blur' }],
  tenantName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }]
}

function resetForm() {
  Object.assign(form, { id: null, tenantCode: '', tenantName: '', contactName: '', contactPhone: '', contactEmail: '', expireTime: null, status: 1, remark: '' })
}

function handleCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateTenant({ ...form })
      ElMessage.success('更新成功')
    } else {
      await createTenant({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${newStatus === 1 ? '启用' : '停用'}租户「${row.tenantName}」吗？`, '确认')
  await updateTenantStatus(row.id, newStatus)
  ElMessage.success('操作成功')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除租户「${row.tenantName}」吗？此操作不可恢复！`, '警告', { type: 'warning' })
  await deleteTenant(row.id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.tenant-list__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.tenant-list__search {
  display: flex;
  gap: 12px;
}
.tenant-list__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
