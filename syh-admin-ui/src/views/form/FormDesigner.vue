<template>
  <div class="page-container">
    <div class="card">
      <div class="event-list__toolbar">
        <h3 style="font-size: 16px; font-weight: 600;">表单模板管理</h3>
        <el-button type="primary" :icon="Plus" @click="showDialog()">新建模板</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="模板名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDialog(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="showFieldDialog(row)">字段管理</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 模板编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingTemplate ? '编辑模板' : '新建模板'" width="500px">
      <el-form :model="templateForm" label-width="80px">
        <el-form-item label="模板名称" required>
          <el-input v-model="templateForm.name" placeholder="如：赛事报名表" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="templateForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="templateForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSaveTemplate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 字段管理弹窗 -->
    <el-dialog v-model="fieldDialogVisible" title="字段管理" width="800px" top="5vh">
      <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
        <span style="font-weight: 600;">模板：{{ currentTemplateName }}</span>
        <el-button type="primary" size="small" :icon="Plus" @click="showAddField">添加字段</el-button>
      </div>
      <el-table :data="fields" v-loading="fieldLoading" stripe size="small">
        <el-table-column prop="fieldKey" label="字段标识" width="120" />
        <el-table-column prop="fieldLabel" label="字段名称" width="120" />
        <el-table-column prop="fieldType" label="类型" width="100" />
        <el-table-column prop="required" label="必填" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.required ? 'danger' : 'info'" size="small">{{ row.required ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="placeholder" label="占位提示" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleDeleteField(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加字段表单 -->
      <div v-if="showFieldForm" style="margin-top: 16px; padding: 16px; background: var(--syh-bg-page); border-radius: var(--syh-radius);">
        <el-form :model="fieldForm" label-width="80px" size="small">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="字段标识"><el-input v-model="fieldForm.fieldKey" placeholder="name" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="字段名称"><el-input v-model="fieldForm.fieldLabel" placeholder="姓名" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="字段类型">
                <el-select v-model="fieldForm.fieldType" style="width: 100%">
                  <el-option label="文本" value="text" />
                  <el-option label="数字" value="number" />
                  <el-option label="单选" value="radio" />
                  <el-option label="多选" value="checkbox" />
                  <el-option label="下拉" value="select" />
                  <el-option label="日期" value="date" />
                  <el-option label="手机号" value="phone" />
                  <el-option label="身份证" value="idcard" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="占位提示"><el-input v-model="fieldForm.placeholder" /></el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="必填">
                <el-switch v-model="fieldForm.required" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <el-button type="primary" @click="handleAddField">添加</el-button>
                <el-button @click="showFieldForm = false">取消</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTemplatePage, createTemplate, updateTemplate, deleteTemplate, getFieldList, addField, deleteField } from '@/api/form'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const submitting = ref(false)
const editingTemplate = ref(null)
const templateForm = reactive({ name: '', description: '', status: 1 })

const fieldDialogVisible = ref(false)
const fieldLoading = ref(false)
const fields = ref([])
const currentTemplateId = ref(null)
const currentTemplateName = ref('')
const showFieldForm = ref(false)
const fieldForm = reactive({ fieldKey: '', fieldLabel: '', fieldType: 'text', placeholder: '', required: 0, sortOrder: 0 })

async function loadData() {
  loading.value = true
  try {
    const res = await getTemplatePage({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* handled */ } finally { loading.value = false }
}

function showDialog(row) {
  editingTemplate.value = row || null
  templateForm.name = row?.name || ''
  templateForm.description = row?.description || ''
  templateForm.status = row?.status ?? 1
  dialogVisible.value = true
}

async function handleSaveTemplate() {
  if (!templateForm.name) return ElMessage.warning('请输入模板名称')
  submitting.value = true
  try {
    if (editingTemplate.value) {
      await updateTemplate(editingTemplate.value.id, templateForm)
      ElMessage.success('保存成功')
    } else {
      await createTemplate(templateForm)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除模板「${row.name}」吗？`, '警告', { type: 'warning' })
  await deleteTemplate(row.id)
  ElMessage.success('已删除')
  loadData()
}

async function showFieldDialog(row) {
  currentTemplateId.value = row.id
  currentTemplateName.value = row.name
  showFieldForm.value = false
  fieldDialogVisible.value = true
  await loadFields()
}

async function loadFields() {
  fieldLoading.value = true
  try {
    const res = await getFieldList(currentTemplateId.value)
    fields.value = res.data || []
  } catch { /* handled */ } finally { fieldLoading.value = false }
}

function showAddField() {
  Object.assign(fieldForm, { fieldKey: '', fieldLabel: '', fieldType: 'text', placeholder: '', required: 0, sortOrder: fields.value.length })
  showFieldForm.value = true
}

async function handleAddField() {
  if (!fieldForm.fieldKey || !fieldForm.fieldLabel) return ElMessage.warning('请填写字段标识和名称')
  await addField({ ...fieldForm, templateId: currentTemplateId.value })
  ElMessage.success('字段已添加')
  showFieldForm.value = false
  loadFields()
}

async function handleDeleteField(row) {
  await ElMessageBox.confirm(`确定删除字段「${row.fieldLabel}」吗？`, '警告', { type: 'warning' })
  await deleteField(row.id)
  ElMessage.success('已删除')
  loadFields()
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
.event-list__pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
