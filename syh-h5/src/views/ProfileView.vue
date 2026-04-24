<template>
  <div class="profile-page">
    <van-nav-bar title="我的" />

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-avatar">
        <van-icon name="user-circle-o" size="60" color="#667eea" />
      </div>
      <div class="user-info">
        <div class="user-name">{{ userInfo.name || '未登录' }}</div>
        <div class="user-phone">{{ userInfo.phone || '点击登录' }}</div>
      </div>
    </div>

    <!-- 会员卡片 -->
    <div class="membership-card" @click="goToMembership">
      <div class="membership-badge">
        <span class="badge-icon">{{ membershipIcon }}</span>
        <span class="badge-text">{{ membershipName }}</span>
      </div>
      <div class="membership-desc">
        <span v-if="hasMembership">{{ membershipExpireText }}</span>
        <span v-else>开通会员享更多权益</span>
      </div>
      <van-icon name="arrow" />
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <van-cell-group inset>
        <van-cell title="我的报名" icon="orders-o" is-link to="/my/registrations" />
        <van-cell title="会员中心" icon="vip-card-o" is-link to="/membership" />
        <van-cell title="我的积分" icon="gold-coin-o" is-link badge="0" />
        <van-cell title="优惠券" icon="coupon-o" is-link badge="0" />
      </van-cell-group>
    </div>

    <div class="menu-section">
      <van-cell-group inset>
        <van-cell title="设置" icon="setting-o" is-link />
        <van-cell title="帮助与反馈" icon="question-o" is-link />
        <van-cell title="关于我们" icon="info-o" is-link />
      </van-cell-group>
    </div>

    <!-- 退出登录 -->
    <div class="logout-section">
      <van-button block round type="danger" plain @click="handleLogout">
        退出登录
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserMembership } from '@/api/membership'
import { showConfirmDialog, showToast } from 'vant'

const router = useRouter()
const userInfo = ref({
  id: 1001,
  name: '张三',
  phone: '138****8888'
})

const membership = ref(null)

const hasMembership = computed(() => {
  return membership.value && membership.value.status === 'ACTIVE'
})

const membershipIcon = computed(() => {
  if (!hasMembership.value) return '🆓'
  const icons = {
    FREE: '🆓',
    BRONZE: '🥉',
    SILVER: '🥈',
    GOLD: '🥇',
    PLATINUM: '💎'
  }
  return icons[membership.value.level] || '🆓'
})

const membershipName = computed(() => {
  if (!hasMembership.value) return '免费会员'
  const names = {
    FREE: '免费会员',
    BRONZE: '铜牌会员',
    SILVER: '银牌会员',
    GOLD: '金牌会员',
    PLATINUM: '白金会员'
  }
  return names[membership.value.level] || '免费会员'
})

const membershipExpireText = computed(() => {
  if (!hasMembership.value) return ''
  return `有效期至 ${membership.value.endDate}`
})

const loadMembership = async () => {
  try {
    const res = await getUserMembership(userInfo.value.id)
    membership.value = res.data
  } catch (error) {
    console.error('加载会员信息失败', error)
  }
}

const goToMembership = () => {
  router.push('/membership')
}

const handleLogout = async () => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    localStorage.removeItem('syh_h5_token')
    showToast('已退出登录')
    router.push('/login')
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadMembership()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 32px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: white;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 14px;
  opacity: 0.9;
}

.membership-card {
  margin: 16px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.2s;
}

.membership-card:active {
  transform: scale(0.98);
}

.membership-badge {
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge-icon {
  font-size: 32px;
}

.badge-text {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.membership-desc {
  flex: 1;
  font-size: 12px;
  color: #999;
}

.menu-section {
  margin-bottom: 16px;
}

.logout-section {
  padding: 24px 16px;
}
</style>
