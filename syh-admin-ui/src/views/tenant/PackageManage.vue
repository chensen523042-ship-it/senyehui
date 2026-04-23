<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon> 新增套餐
      </el-button>
    </div>

    <el-table :data="list" border stripe style="width:100%;margin-top:12px">
      <el-table-column prop="packageName" label="套餐名称" min-width="130" />
      <el-table-column label="最大用户数" width="110" align="center">
        <template #default="{ row }">{{ row.maxUsers === 0 ? '不限' : row.maxUsers }}</template>
      </el-table-column>
      <el-table-column label="最大赛事数" width="110" align="center">
        <template #default="{ row }">{{ row.maxEvents === 0 ? '不限' : row.maxEvents }}</template>
      </el-table-column>
      <el-table-column label="月费(¥)" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.price == 0 ? 'success' : 'primary'">
            {{ row.price == 0 ? '免费' : `¥${row.price}` }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑套餐' : '新增套餐'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="套餐名称" prop="packageName">
          <el-input v-model="form.packageName" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="最大用户数">
          <el-input-number v-model="form.maxUsers" :min="0" placeholder="0=不限" style="width:160px" />
          <span class="hint">0 表示不限</span>
        </el-form-item>
        <el-form-item label="最大赛事数">
          <el-input-number v-model="form.maxEvents" :min="0" placeholder="0=不限" style="width:160px" />
          <span class="hint">0 表示不限</span>
        </el-form-item>
        <el-form-item label="月费(¥)">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width:160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPackageList, createPackage, updatePackage, deletePackage } from '@/api/tenantPackage'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const emptyForm = () => ({ packageName: '', maxUsers: 0, maxEvents: 0, price: 0, status: 1, remark: '' })
const form = ref(emptyForm())

const rules = {
  packageName: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }]
}

async function loadList() {
  const res = await getPackageList()
  list.value = res.data
}

function openCreate() {
  isEdit.value = false
  form.value = emptyForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updatePackage(form.value)
    } else {
      await createPackage(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除套餐「${row.packageName}」吗？`, '警告', { type: 'warning' })
  await deletePackage(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; }
.hint { margin-left: 8px; font-size: 12px; color: #999; }
</style>
