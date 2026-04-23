<template>
  <div class="page-container">
    <div class="card">
      <!-- 搜索工具栏 -->
      <div class="user-manage__toolbar">
        <div class="user-manage__search">
          <el-input v-model="searchKeyword" placeholder="用户名 / 昵称 / 手机号" clearable style="width: 260px" :prefix-icon="Search" @keyup.enter="loadData" />
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </div>
        <el-button v-permission="'system:user:add'" type="primary" :icon="Plus" @click="handleCreate">新增用户</el-button>
      </div>

      <!-- 用户列表 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最近登录" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:user:edit'" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'system:user:edit'" link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button v-permission="'system:user:resetPwd'" link type="info" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button v-permission="'system:user:delete'" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="user-manage__pagination">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-alert v-if="!isEdit" title="新用户默认密码为 123456" type="info" :closable="false" show-icon style="margin-bottom: 0;" />
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
import { getUserPage, getUser, createUser, updateUser, deleteUser, updateUserStatus, resetUserPassword } from '@/api/user'
import { getRoleList } from '@/api/role'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

// ==================== 列表 ====================
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchStatus = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value ?? undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchKeyword.value = ''
  searchStatus.value = null
  pageNum.value = 1
  loadData()
}

// ==================== 弹窗表单 ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const roleOptions = ref([])

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  roleIds: [],
  status: 1
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 30, message: '长度 2 ~ 30 个字符', trigger: 'blur' }
  ],
  nickname: [
    { max: 30, message: '长度不超过 30 个字符', trigger: 'blur' }
  ]
}

function resetForm() {
  form.id = null
  form.username = ''
  form.nickname = ''
  form.phone = ''
  form.email = ''
  form.roleIds = []
  form.status = 1
}

function handleCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleEdit(row) {
  isEdit.value = true
  resetForm()
  dialogVisible.value = true
  try {
    const res = await getUser(row.id)
    const u = res.data?.user || res.data
    form.id = u.id
    form.username = u.username
    form.nickname = u.nickname || ''
    form.phone = u.phone || ''
    form.email = u.email || ''
    form.status = u.status
    form.roleIds = res.data?.roleIds || []
  } catch { /* handled */ }
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch { return }

  submitLoading.value = true
  try {
    const payload = {
      username: form.username,
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      status: form.status,
      roleIds: form.roleIds
    }
    if (isEdit.value) {
      payload.id = form.id
      await updateUser(payload)
      ElMessage.success('更新成功')
    } else {
      await createUser(payload)
      ElMessage.success('创建成功，默认密码为 123456')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

// ==================== 操作 ====================
async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定${action}用户「${row.username}」吗？`, '确认')
  await updateUserStatus(row.id, newStatus)
  ElMessage.success(`已${action}`)
  loadData()
}

async function handleResetPwd(row) {
  await ElMessageBox.confirm(`确定重置「${row.username}」的密码为 123456 吗？`, '重置密码', { type: 'warning' })
  await resetUserPassword(row.id)
  ElMessage.success('密码已重置为 123456')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？此操作不可恢复！`, '警告', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('已删除')
  loadData()
}

// ==================== 初始化 ====================
onMounted(async () => {
  // 加载角色选项
  try {
    const res = await getRoleList()
    roleOptions.value = res.data || []
  } catch { /* ignore */ }
  loadData()
})
</script>

<style scoped>
.user-manage__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.user-manage__search {
  display: flex;
  gap: 12px;
}
.user-manage__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
