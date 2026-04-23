<template>
  <div class="page-container">
    <!-- 顶部导航 -->
    <van-nav-bar :title="event?.title || '赛事详情'" left-arrow @click-left="$router.back()" />

    <van-skeleton :loading="!event" :row="8" style="padding: 16px">
      <template #default>
        <!-- 封面 -->
        <div class="detail-cover">
          <img :src="event.coverImage || defaultCover" alt="" />
          <div class="detail-cover__overlay">
            <van-tag :type="event.status === 3 ? 'success' : 'primary'" size="large">
              {{ statusText(event.status) }}
            </van-tag>
          </div>
        </div>

        <!-- 基础信息 -->
        <div class="detail-info">
          <h1 class="detail-info__title">{{ event.title }}</h1>
          <div class="detail-info__price">
            <template v-if="event.price > 0">
              <span class="detail-info__price-label">报名费</span>
              <span class="detail-info__price-value">¥{{ event.price }}</span>
            </template>
            <van-tag v-else type="success" size="large">免费</van-tag>
          </div>
        </div>

        <!-- 详细信息卡片 -->
        <van-cell-group inset class="detail-card">
          <van-cell icon="clock-o" title="活动时间" :label="`${event.startTime || '待定'} ~ ${event.endTime || '待定'}`" />
          <van-cell icon="location-o" title="活动地点" :label="event.location || '待定'" />
          <van-cell icon="friends-o" title="报名人数">
            <template #value>
              <span class="detail-participants">{{ event.currentParticipants || 0 }}</span>
              <span v-if="event.maxParticipants > 0"> / {{ event.maxParticipants }}</span>
            </template>
          </van-cell>
          <van-cell v-if="event.regStartTime" icon="underway-o" title="报名时间"
            :label="`${event.regStartTime} ~ ${event.regEndTime}`" />
        </van-cell-group>

        <!-- 活动详情 -->
        <div class="detail-section">
          <h3 class="detail-section__title">活动详情</h3>
          <div class="detail-section__content">{{ event.description || '暂无详情' }}</div>
        </div>

        <!-- 底部报名按钮 -->
        <div class="detail-action">
          <van-button
            v-if="event.status === 3"
            type="primary"
            block
            round
            size="large"
            @click="$router.push(`/register/${event.id}`)"
          >
            立即报名
            <template v-if="event.price > 0"> · ¥{{ event.price }}</template>
          </van-button>
          <van-button v-else type="default" block round size="large" disabled>
            {{ event.status === 2 ? '报名未开始' : '报名已结束' }}
          </van-button>
        </div>
      </template>
    </van-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getEventDetail } from '@/api'

const route = useRoute()
const event = ref(null)
const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjI1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48bGluZWFyR3JhZGllbnQgaWQ9ImciIHgxPSIwJSIgeTE9IjAlIiB4Mj0iMTAwJSIgeTI9IjEwMCUiPjxzdG9wIG9mZnNldD0iMCUiIHN0b3AtY29sb3I9IiM0RjZFRjciLz48c3RvcCBvZmZzZXQ9IjEwMCUiIHN0b3AtY29sb3I9IiM3QzNBRUQiLz48L2xpbmVhckdyYWRpZW50PjwvZGVmcz48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjI1MCIgZmlsbD0idXJsKCNnKSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmaWxsPSIjZmZmIiBmb250LXNpemU9IjMwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuajruihpeaxhzwvdGV4dD48L3N2Zz4='

function statusText(s) {
  return { 0: '草稿', 1: '待审核', 2: '已发布', 3: '报名中', 4: '进行中', 5: '已结束', 6: '已取消' }[s] || '未知'
}

onMounted(async () => {
  try {
    const res = await getEventDetail(route.params.id)
    event.value = res.data
  } catch (e) {
    console.error('加载失败', e)
  }
})
</script>

<style scoped>
.detail-cover {
  position: relative;
  height: 220px;
}
.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.detail-cover__overlay {
  position: absolute;
  top: 16px;
  right: 16px;
}

.detail-info {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.detail-info__title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
  flex: 1;
  margin-right: 12px;
}
.detail-info__price-label {
  font-size: 12px;
  color: var(--syh-text-secondary);
}
.detail-info__price-value {
  font-size: 24px;
  font-weight: 700;
  color: #FF4D4F;
}

.detail-card {
  margin: 0 16px 16px;
}

.detail-participants {
  font-weight: 700;
  color: var(--syh-primary);
  font-size: 18px;
}

.detail-section {
  margin: 0 16px 16px;
  background: var(--syh-card);
  border-radius: 12px;
  padding: 16px;
}
.detail-section__title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid var(--syh-primary);
}
.detail-section__content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--syh-text-secondary);
  white-space: pre-wrap;
}

.detail-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: var(--syh-card);
  box-shadow: 0 -2px 12px rgba(0,0,0,0.08);
  z-index: 100;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
}
</style>
