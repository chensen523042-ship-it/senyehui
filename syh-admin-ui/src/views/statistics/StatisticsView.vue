<template>
  <div class="statistics">
    <!-- 总览卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="card in cards" :key="card.label" :style="{ background: card.color }">
        <div class="stat-card__icon">{{ card.icon }}</div>
        <div class="stat-card__body">
          <div class="stat-card__value">{{ card.value }}</div>
          <div class="stat-card__label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="charts-row">
      <div class="chart-box">
        <div class="chart-box__title">赛事状态分布</div>
        <div ref="pieRef" class="chart-box__canvas"></div>
      </div>
      <div class="chart-box chart-box--wide">
        <div class="chart-box__title">近 6 个月报名趋势</div>
        <div ref="lineRef" class="chart-box__canvas"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getEventStatus, getRegistrationTrend } from '@/api/statistics'

const overview = ref({})
const pieRef = ref(null)
const lineRef = ref(null)
let pieChart, lineChart

const cards = computed(() => [
  { label: '赛事总数', value: overview.value.totalEvents ?? '-', icon: '🏆', color: 'linear-gradient(135deg,#6366f1,#818cf8)' },
  { label: '报名总数', value: overview.value.totalRegistrations ?? '-', icon: '📋', color: 'linear-gradient(135deg,#0ea5e9,#38bdf8)' },
  { label: '本月收入', value: overview.value.monthlyRevenue != null ? `¥${overview.value.monthlyRevenue}` : '-', icon: '💰', color: 'linear-gradient(135deg,#10b981,#34d399)' },
  { label: '签到率', value: overview.value.checkInRate != null ? `${overview.value.checkInRate}%` : '-', icon: '✅', color: 'linear-gradient(135deg,#f59e0b,#fbbf24)' },
])

function initPie(data) {
  if (!pieChart) pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#666' } },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '45%'],
      data,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } }
    }]
  })
}

function initLine(months, counts) {
  if (!lineChart) lineChart = echarts.init(lineRef.value)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: months, boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      name: '报名人数',
      type: 'line',
      smooth: true,
      data: counts,
      areaStyle: { opacity: 0.3 },
      lineStyle: { width: 3, color: '#6366f1' },
      itemStyle: { color: '#6366f1' }
    }]
  })
}

const onResize = () => { pieChart?.resize(); lineChart?.resize() }

onMounted(async () => {
  const [ov, es, rt] = await Promise.all([getOverview(), getEventStatus(), getRegistrationTrend()])
  overview.value = ov.data
  initPie(es.data)
  initLine(rt.data.months, rt.data.counts)
  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.statistics { padding: 4px 0; }

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,.12);
}
.stat-card__icon { font-size: 32px; line-height: 1; }
.stat-card__value { font-size: 26px; font-weight: 700; line-height: 1.2; }
.stat-card__label { font-size: 13px; opacity: .85; margin-top: 4px; }

.charts-row {
  display: flex;
  gap: 16px;
}

.chart-box {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.06);
  flex: 1;
}
.chart-box--wide { flex: 2; }
.chart-box__title { font-size: 15px; font-weight: 600; color: #374151; margin-bottom: 12px; }
.chart-box__canvas { height: 280px; }

@media (max-width: 900px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .charts-row { flex-direction: column; }
}
</style>
