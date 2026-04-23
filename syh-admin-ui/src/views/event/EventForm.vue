<template>
  <div class="page-container">
    <div class="card">
      <div class="card__header">
        <h3>{{ isEdit ? '编辑赛事' : '创建赛事' }}</h3>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="赛事名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入赛事名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="赛事类型" prop="eventType">
          <el-radio-group v-model="form.eventType">
            <el-radio :value="1">赛事</el-radio>
            <el-radio :value="2">活动</el-radio>
            <el-radio :value="3">培训</el-radio>
            <el-radio :value="4">露营</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="赛事分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="封面图 URL" />
        </el-form-item>

        <el-divider content-position="left">时间与地点</el-divider>

        <el-form-item label="活动时间" prop="startTime">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            @change="onDateRangeChange"
          />
        </el-form-item>

        <el-form-item label="报名时间">
          <el-date-picker
            v-model="regDateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="报名开始"
            end-placeholder="报名截止"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            @change="onRegDateRangeChange"
          />
        </el-form-item>

        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="如：北京奥林匹克森林公园" />
        </el-form-item>

        <el-form-item label="详细地址">
          <el-input v-model="form.address" placeholder="详细地址" />
        </el-form-item>

        <el-divider content-position="left">报名设置</el-divider>

        <el-form-item label="最大参与人数">
          <el-input-number v-model="form.maxParticipants" :min="0" :max="100000" :step="10" />
          <span style="margin-left: 8px; color: var(--syh-text-secondary); font-size: 13px;">0 = 不限制</span>
        </el-form-item>

        <el-form-item label="报名费(元)">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" />
        </el-form-item>

        <el-divider content-position="left">活动详情</el-divider>

        <el-form-item label="活动描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="活动详情描述..." />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '创建赛事' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createEvent, updateEvent, getEvent } from '@/api/event'
import { getCategoryList } from '@/api/event'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitting = ref(false)
const categories = ref([])
const dateRange = ref(null)
const regDateRange = ref(null)

const form = reactive({
  title: '',
  eventType: 1,
  categoryId: null,
  coverImage: '',
  startTime: '',
  endTime: '',
  regStartTime: '',
  regEndTime: '',
  location: '',
  address: '',
  maxParticipants: 0,
  price: 0,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
  eventType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

function onDateRangeChange(val) {
  if (val) {
    form.startTime = val[0]
    form.endTime = val[1]
  } else {
    form.startTime = ''
    form.endTime = ''
  }
}

function onRegDateRangeChange(val) {
  if (val) {
    form.regStartTime = val[0]
    form.regEndTime = val[1]
  } else {
    form.regStartTime = ''
    form.regEndTime = ''
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateEvent(route.params.id, form)
      ElMessage.success('保存成功')
    } else {
      await createEvent(form)
      ElMessage.success('创建成功')
    }
    router.push('/event')
  } catch { /* handled */ } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  // 加载分类
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch { /* ignore */ }

  // 编辑模式加载数据
  if (isEdit.value) {
    try {
      const res = await getEvent(route.params.id)
      Object.assign(form, res.data)
      if (form.startTime && form.endTime) {
        dateRange.value = [form.startTime, form.endTime]
      }
      if (form.regStartTime && form.regEndTime) {
        regDateRange.value = [form.regStartTime, form.regEndTime]
      }
    } catch {
      ElMessage.error('加载赛事数据失败')
      router.push('/event')
    }
  }
})
</script>

<style scoped>
.card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card__header h3 {
  font-size: 18px;
  font-weight: 600;
}
</style>
