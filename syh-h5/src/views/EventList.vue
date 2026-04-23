<template>
  <div class="page-container">
    <!-- 顶部搜索 -->
    <div class="event-header">
      <div class="event-header__bg"></div>
      <div class="event-header__content">
        <h1 class="event-header__title">森野汇</h1>
        <p class="event-header__subtitle">发现精彩户外赛事</p>
        <van-search
          v-model="keyword"
          placeholder="搜索赛事名称、地点"
          shape="round"
          background="transparent"
          @search="onSearch"
        />
      </div>
    </div>

    <!-- 分类标签 -->
    <van-tabs v-model:active="activeType" shrink sticky offset-top="0" @change="onTypeChange">
      <van-tab title="全部" :name="0" />
      <van-tab title="赛事" :name="1" />
      <van-tab title="活动" :name="2" />
      <van-tab title="培训" :name="3" />
      <van-tab title="露营" :name="4" />
    </van-tabs>

    <!-- 赛事列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadMore">
        <div class="event-list">
          <div
            v-for="event in events"
            :key="event.id"
            class="event-card"
            @click="$router.push(`/event/${event.id}`)"
          >
            <div class="event-card__cover">
              <img :src="event.coverImage || defaultCover" alt="" />
              <div class="event-card__badge">
                <van-tag :type="event.status === 3 ? 'success' : 'primary'" size="medium">
                  {{ event.status === 3 ? '报名中' : '即将开始' }}
                </van-tag>
              </div>
              <div v-if="event.price > 0" class="event-card__price">
                ¥{{ event.price }}
              </div>
              <div v-else class="event-card__price event-card__price--free">免费</div>
            </div>
            <div class="event-card__body">
              <h3 class="event-card__title">{{ event.title }}</h3>
              <div class="event-card__meta">
                <van-icon name="clock-o" size="14" />
                <span>{{ formatTime(event.startTime) }}</span>
              </div>
              <div class="event-card__meta">
                <van-icon name="location-o" size="14" />
                <span>{{ event.location }}</span>
              </div>
              <div class="event-card__footer">
                <div class="event-card__participants">
                  <van-icon name="friends-o" size="14" />
                  <span>{{ event.currentParticipants || 0 }}人已报名</span>
                  <span v-if="event.maxParticipants > 0" class="event-card__quota">
                    / {{ event.maxParticipants }}
                  </span>
                </div>
                <van-tag :type="typeTagType(event.eventType)" plain size="small">
                  {{ typeText(event.eventType) }}
                </van-tag>
              </div>
            </div>
          </div>
        </div>

        <van-empty v-if="!loading && events.length === 0" description="暂无赛事" image="search" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getEventList } from '@/api'

const keyword = ref('')
const activeType = ref(0)
const events = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48bGluZWFyR3JhZGllbnQgaWQ9ImciIHgxPSIwJSIgeTE9IjAlIiB4Mj0iMTAwJSIgeTI9IjEwMCUiPjxzdG9wIG9mZnNldD0iMCUiIHN0b3AtY29sb3I9IiM0RjZFRjciLz48c3RvcCBvZmZzZXQ9IjEwMCUiIHN0b3AtY29sb3I9IiM3QzNBRUQiLz48L2xpbmVhckdyYWRpZW50PjwvZGVmcz48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0idXJsKCNnKSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmaWxsPSIjZmZmIiBmb250LXNpemU9IjI0IiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuajruihpeaxhzwvdGV4dD48L3N2Zz4='

async function loadMore() {
  try {
    const params = { pageNum: pageNum.value, pageSize: 10 }
    if (keyword.value) params.keyword = keyword.value
    if (activeType.value > 0) params.eventType = activeType.value

    const res = await getEventList(params)
    const records = res.data?.records || []
    if (pageNum.value === 1) {
      events.value = records
    } else {
      events.value.push(...records)
    }
    pageNum.value++
    finished.value = records.length < 10
  } catch {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  pageNum.value = 1
  finished.value = false
  loadMore()
}

function onSearch() {
  pageNum.value = 1
  events.value = []
  finished.value = false
  loading.value = true
  loadMore()
}

function onTypeChange() {
  onSearch()
}

function formatTime(t) {
  return t ? t.replace(/:00$/, '') : '待定'
}

function typeTagType(type) {
  return { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' }[type] || 'default'
}
function typeText(type) {
  return { 1: '赛事', 2: '活动', 3: '培训', 4: '露营' }[type] || '-'
}
</script>

<style scoped>
.event-header {
  position: relative;
  padding: 40px 16px 20px;
  overflow: hidden;
}
.event-header__bg {
  position: absolute;
  inset: 0;
  background: var(--syh-gradient);
  border-radius: 0 0 24px 24px;
}
.event-header__content {
  position: relative;
  z-index: 1;
}
.event-header__title {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}
.event-header__subtitle {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
  margin-bottom: 16px;
}

.event-list {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.event-card {
  background: var(--syh-card);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.event-card:active {
  transform: scale(0.98);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.event-card__cover {
  position: relative;
  height: 160px;
  overflow: hidden;
}
.event-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.event-card__badge {
  position: absolute;
  top: 12px;
  left: 12px;
}
.event-card__price {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0,0,0,0.6);
  color: #FF6B6B;
  font-weight: 700;
  font-size: 16px;
  padding: 4px 12px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
}
.event-card__price--free {
  color: var(--syh-success);
}

.event-card__body {
  padding: 14px 16px;
}
.event-card__title {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 10px;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.event-card__meta {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--syh-text-secondary);
  font-size: 13px;
  margin-bottom: 6px;
}
.event-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding-top: 10px;
  border-top: 1px solid var(--syh-border);
}
.event-card__participants {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--syh-text-secondary);
}
.event-card__quota {
  color: #ccc;
}
</style>
