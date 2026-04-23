<template>
  <div class="dashboard">
    <h2 class="dashboard__title">工作台</h2>

    <!-- 统计卡片 -->
    <div class="dashboard__stats">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <div class="stat-card__header">
          <div class="stat-card__icon" :style="{ background: stat.gradient }">
            <el-icon :size="22"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-card__trend" :class="stat.up ? 'stat-card__trend--up' : 'stat-card__trend--down'">
            <el-icon><Top v-if="stat.up" /><Bottom v-else /></el-icon>
            {{ stat.percent }}%
          </div>
        </div>
        <div class="stat-card__value">{{ stat.value }}</div>
        <div class="stat-card__label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="dashboard__charts">
      <!-- 报名趋势折线图 -->
      <div class="card dashboard__chart-card">
        <div class="card__header">
          <h3>本周报名趋势</h3>
        </div>
        <div ref="trendChartRef" class="dashboard__chart"></div>
      </div>

      <!-- 赛事类型分布饼图 -->
      <div class="card dashboard__chart-card">
        <div class="card__header">
          <h3>赛事类型分布</h3>
        </div>
        <div ref="pieChartRef" class="dashboard__chart"></div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="dashboard__content">
      <!-- 近期赛事 -->
      <div class="card dashboard__recent">
        <div class="card__header">
          <h3>近期赛事</h3>
          <el-button type="primary" link @click="$router.push('/event')">查看全部</el-button>
        </div>
        <el-table :data="recentEvents" stripe style="width: 100%">
          <el-table-column prop="title" label="赛事名称" />
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="currentParticipants" label="报名人数" width="100" align="center" />
        </el-table>
      </div>

      <!-- 快捷操作 -->
      <div class="card dashboard__quick">
        <div class="card__header">
          <h3>快捷操作</h3>
        </div>
        <div class="dashboard__quick-grid">
          <div
            v-for="action in quickActions"
            :key="action.label"
            class="dashboard__quick-item"
            @click="$router.push(action.path)"
          >
            <div class="dashboard__quick-icon" :style="{ background: action.gradient }">
              <el-icon :size="20"><component :is="action.icon" /></el-icon>
            </div>
            <span>{{ action.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getEventPage } from '@/api/event'
import { getRegistrationPage, getIncome } from '@/api/registration'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

const stats = ref([
  { label: '赛事总数', value: '0', icon: 'Trophy', gradient: 'linear-gradient(135deg, #3b82f6, #6366f1)', percent: 12, up: true },
  { label: '报名人数', value: '0', icon: 'User', gradient: 'linear-gradient(135deg, #10b981, #34d399)', percent: 8, up: true },
  { label: '本月收入', value: '¥0', icon: 'Wallet', gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)', percent: 5, up: false },
  { label: '签到率', value: '0%', icon: 'CircleCheck', gradient: 'linear-gradient(135deg, #8b5cf6, #a78bfa)', percent: 3, up: true }
])

const recentEvents = ref([])

const quickActions = [
  { label: '创建赛事', icon: 'Plus', path: '/event/create', gradient: 'linear-gradient(135deg, #3b82f6, #6366f1)' },
  { label: '报名管理', icon: 'DocumentChecked', path: '/registration', gradient: 'linear-gradient(135deg, #10b981, #34d399)' },
  { label: '表单设计', icon: 'EditPen', path: '/form', gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)' },
  { label: '用户管理', icon: 'UserFilled', path: '/system/user', gradient: 'linear-gradient(135deg, #8b5cf6, #a78bfa)' }
]

function statusTagType(status) {
  return { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: '', 5: 'info', 6: 'danger' }[status] || 'info'
}

function statusText(status) {
  return { 0: '草稿', 1: '待审核', 2: '已发布', 3: '报名中', 4: '进行中', 5: '已结束', 6: '已取消' }[status] || '未知'
}

// 初始化报名趋势折线图
function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const days = []
  const now = new Date()
  for (let i = 6; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    days.push(`${d.getMonth() + 1}/${d.getDate()}`)
  }

  // 演示数据，实际项目中从 API 获取
  const data = [12, 28, 18, 35, 42, 30, 56]

  trendChart.setOption({
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(30, 41, 59, 0.9)', borderColor: 'transparent', textStyle: { color: '#fff' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '12%', containLabel: true },
    xAxis: {
      type: 'category',
      data: days,
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af' }
    },
    series: [{
      name: '报名人数',
      data,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color: '#3b82f6' },
        { offset: 1, color: '#8b5cf6' }
      ])},
      itemStyle: { color: '#3b82f6', borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59, 130, 246, 0.25)' },
          { offset: 1, color: 'rgba(59, 130, 246, 0.02)' }
        ])
      }
    }]
  })
}

// 初始化赛事类型饼图
function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)

  pieChart.setOption({
    tooltip: { trigger: 'item', backgroundColor: 'rgba(30, 41, 59, 0.9)', borderColor: 'transparent', textStyle: { color: '#fff' } },
    legend: { bottom: '0', left: 'center', textStyle: { color: '#6b7280' } },
    series: [{
      name: '赛事类型',
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
      label: { show: true, formatter: '{b}\n{d}%', color: '#4b5563', fontSize: 12 },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.2)' }
      },
      data: [
        { value: 8, name: '马拉松', itemStyle: { color: '#3b82f6' } },
        { value: 5, name: '越野赛', itemStyle: { color: '#10b981' } },
        { value: 4, name: '铁人三项', itemStyle: { color: '#f59e0b' } },
        { value: 3, name: '自行车赛', itemStyle: { color: '#8b5cf6' } },
        { value: 2, name: '其他', itemStyle: { color: '#6b7280' } }
      ]
    }]
  })
}

// 响应窗口 resize
function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  // 加载赛事数据
  try {
    const res = await getEventPage({ pageNum: 1, pageSize: 5 })
    recentEvents.value = res.data?.records || []
    stats.value[0].value = String(res.data?.total || 0)
  } catch { /* 容错 */ }

  // 加载报名和收入统计
  try {
    const regRes = await getRegistrationPage({ pageNum: 1, pageSize: 1 })
    stats.value[1].value = String(regRes.data?.total || 0)
  } catch { /* 容错 */ }
  try {
    const incomeRes = await getIncome(1)
    const amount = incomeRes.data || 0
    stats.value[2].value = `¥${amount}`
  } catch { /* 容错 */ }

  // 初始化图表
  await nextTick()
  initTrendChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.dashboard__title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--syh-text-primary);
}

/* 统计卡片 */
.dashboard__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.stat-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-card__trend {
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-card__trend--up { color: var(--syh-success); }
.stat-card__trend--down { color: var(--syh-danger); }

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: var(--syh-text-primary);
  margin-bottom: 4px;
}

.stat-card__label {
  font-size: 13px;
  color: var(--syh-text-secondary);
}

/* 图表区域 */
.dashboard__charts {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.dashboard__chart-card {
  min-height: 320px;
}

.dashboard__chart {
  height: 260px;
}

/* 内容区域 */
.dashboard__content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
}

.card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card__header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--syh-text-primary);
}

/* 快捷操作 */
.dashboard__quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.dashboard__quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  border-radius: var(--syh-radius);
  border: 1px solid var(--syh-border-light);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  color: var(--syh-text-regular);
}

.dashboard__quick-item:hover {
  border-color: var(--syh-primary);
  background: var(--syh-primary-bg);
  transform: translateY(-2px);
}

.dashboard__quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

@media (max-width: 1200px) {
  .dashboard__stats { grid-template-columns: repeat(2, 1fr); }
  .dashboard__charts { grid-template-columns: 1fr; }
  .dashboard__content { grid-template-columns: 1fr; }
}
</style>
