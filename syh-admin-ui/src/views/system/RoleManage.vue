<template>
  <div class="page-container">
    <div class="card">
      <div class="role-manage__toolbar">
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增角色</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="handleAssignPermissions(row)">分配权限</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="如 admin、operator" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="520px" destroy-on-close>
      <el-tree
        ref="permTreeRef"
        :data="permTree"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermIds"
        highlight-current
        style="max-height: 400px; overflow-y: auto;"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSubmitLoading" @click="handleSavePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getRoleList, getRole, createRole, updateRole, deleteRole, assignPermissions } from '@/api/role'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data || []
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

// ==================== 新增/编辑 ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, roleCode: '', roleName: '', sortOrder: 0, status: 1, remark: '' })

const formRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

function handleCreate() {
  isEdit.value = false
  Object.assign(form, { id: null, roleCode: '', roleName: '', sortOrder: 0, status: 1, remark: '' })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, roleCode: row.roleCode, roleName: row.roleName, sortOrder: row.sortOrder, status: row.status, remark: row.remark || '' })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateRole({ ...form })
      ElMessage.success('更新成功')
    } else {
      await createRole({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？`, '警告', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('已删除')
  loadData()
}

// ==================== 分配权限 ====================
const permDialogVisible = ref(false)
const permSubmitLoading = ref(false)
const permTreeRef = ref(null)
const permTree = ref([])
const checkedPermIds = ref([])
const currentRoleId = ref(null)

function buildTree(list) {
  const map = {}
  list.forEach(p => { map[p.id] = { ...p, children: [] } })
  const roots = []
  list.forEach(p => {
    if (p.parentId && map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    } else {
      roots.push(map[p.id])
    }
  })
  return roots
}

async function handleAssignPermissions(row) {
  currentRoleId.value = row.id
  try {
    const [allRes, roleRes] = await Promise.all([
      request.get('/api/system/role/permissions/all'),
      getRole(row.id)
    ])
    permTree.value = buildTree(allRes.data || [])
    checkedPermIds.value = roleRes.data?.permissionIds || []
  } catch { /* handled */ }
  permDialogVisible.value = true
}

async function handleSavePermissions() {
  permSubmitLoading.value = true
  try {
    const checked = permTreeRef.value.getCheckedKeys()
    const halfChecked = permTreeRef.value.getHalfCheckedKeys()
    await assignPermissions(currentRoleId.value, [...checked, ...halfChecked])
    ElMessage.success('权限已保存')
    permDialogVisible.value = false
  } catch { /* handled */ } finally {
    permSubmitLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.role-manage__toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}
</style>
