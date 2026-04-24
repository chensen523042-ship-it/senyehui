<template>
  <div class="membership-center">
    <!-- 会员卡片 -->
    <div class="membership-card" :class="`level-${membershipLevel}`">
      <div class="card-header">
        <div class="level-badge">
          <span class="level-icon">{{ levelIcon }}</span>
          <span class="level-name">{{ levelName }}</span>
        </div>
        <div class="card-actions">
          <button v-if="!hasMembership" class="btn-upgrade" @click="showUpgradeDialog">开通会员</button>
          <button v-else-if="canUpgrade" class="btn-upgrade" @click="showUpgradeDialog">升级会员</button>
        </div>
      </div>

      <div class="card-body">
        <div class="user-info">
          <div class="user-name">{{ userInfo.name || '会员用户' }}</div>
          <div class="user-id">ID: {{ userInfo.id }}</div>
        </div>

        <div v-if="hasMembership" class="membership-info">
          <div class="info-item">
            <span class="label">有效期至</span>
            <span class="value">{{ membership.endDate }}</span>
          </div>
          <div class="info-item">
            <span class="label">自动续费</span>
            <span class="value">{{ membership.autoRenew ? '已开启' : '未开启' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 会员权益 -->
    <div class="benefits-section">
      <div class="section-title">会员权益</div>
      <div class="benefits-grid">
        <div v-for="benefit in benefits" :key="benefit.code" class="benefit-item">
          <div class="benefit-icon">{{ benefit.icon }}</div>
          <div class="benefit-name">{{ benefit.name }}</div>
          <div class="benefit-desc">{{ benefit.description }}</div>
          <div v-if="benefit.quota" class="benefit-quota">
            剩余 {{ benefit.remaining }}/{{ benefit.total }}
          </div>
        </div>
      </div>
    </div>

    <!-- 会员等级说明 -->
    <div class="levels-section">
      <div class="section-title">会员等级</div>
      <div class="levels-list">
        <div v-for="level in membershipLevels" :key="level.code"
             class="level-card"
             :class="{ active: level.code === membershipLevel }"
             @click="selectLevel(level)">
          <div class="level-header">
            <span class="level-icon">{{ level.icon }}</span>
            <span class="level-name">{{ level.name }}</span>
          </div>
          <div class="level-price">¥{{ level.price }}/年</div>
          <div class="level-benefits">
            <div v-for="(benefit, index) in level.benefits" :key="index" class="benefit-line">
              ✓ {{ benefit }}
            </div>
          </div>
          <button v-if="level.code !== membershipLevel" class="btn-select">
            {{ hasMembership ? '升级' : '开通' }}
          </button>
          <div v-else class="current-badge">当前等级</div>
        </div>
      </div>
    </div>

    <!-- 升级对话框 -->
    <div v-if="upgradeDialogVisible" class="dialog-overlay" @click="upgradeDialogVisible = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>{{ hasMembership ? '升级会员' : '开通会员' }}</h3>
          <button class="btn-close" @click="upgradeDialogVisible = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="selected-level">
            <div class="level-info">
              <span class="level-icon">{{ selectedLevel.icon }}</span>
              <span class="level-name">{{ selectedLevel.name }}</span>
            </div>
            <div class="level-price">¥{{ selectedLevel.price }}</div>
          </div>
          <div class="benefits-summary">
            <div v-for="(benefit, index) in selectedLevel.benefits" :key="index" class="benefit-item">
              ✓ {{ benefit }}
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="upgradeDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="confirmPurchase">确认购买</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserMembership, purchaseMembership, getMembershipConfigList } from '@/api/membership'
import { ElMessage } from 'element-plus'

const router = useRouter()
const membership = ref(null)
const benefits = ref([])
const upgradeDialogVisible = ref(false)
const selectedLevel = ref(null)
const membershipLevels = ref([])

const userInfo = ref({
  id: 1001,
  name: '张三'
})

const hasMembership = computed(() => membership.value && membership.value.status === 'ACTIVE')
const membershipLevel = computed(() => membership.value?.level || 'FREE')
const canUpgrade = computed(() => {
  if (!hasMembership.value) return false
  const levels = ['FREE', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM']
  const currentIndex = levels.indexOf(membershipLevel.value)
  return currentIndex < levels.length - 1
})

const levelIcon = computed(() => {
  const icons = {
    FREE: '🆓',
    BRONZE: '🥉',
    SILVER: '🥈',
    GOLD: '🥇',
    PLATINUM: '💎'
  }
  return icons[membershipLevel.value] || '🆓'
})

const levelName = computed(() => {
  const names = {
    FREE: '免费会员',
    BRONZE: '铜牌会员',
    SILVER: '银牌会员',
    GOLD: '金牌会员',
    PLATINUM: '白金会员'
  }
  return names[membershipLevel.value] || '免费会员'
})

const loadMembership = async () => {
  try {
    const res = await getUserMembership(userInfo.value.id)
    membership.value = res.data
  } catch (error) {
    console.error('加载会员信息失败', error)
  }
}

const loadMembershipLevels = async () => {
  try {
    const res = await getMembershipConfigList()
    membershipLevels.value = res.data.map(config => ({
      code: config.level,
      name: config.name,
      price: config.price,
      icon: getLevelIcon(config.level),
      benefits: parseBenefits(config.benefits)
    }))
  } catch (error) {
    console.error('加载会员等级失败', error)
  }
}

const getLevelIcon = (level) => {
  const icons = {
    FREE: '🆓',
    BRONZE: '🥉',
    SILVER: '🥈',
    GOLD: '🥇',
    PLATINUM: '💎'
  }
  return icons[level] || '🆓'
}

const parseBenefits = (benefitsJson) => {
  try {
    const benefits = JSON.parse(benefitsJson)
    const result = []
    if (benefits.registration_discount) {
      result.push(`报名享${(benefits.registration_discount * 100).toFixed(0)}折优惠`)
    }
    if (benefits.free_registration_count) {
      result.push(`${benefits.free_registration_count}次免费报名`)
    }
    return result
  } catch {
    return []
  }
}

const showUpgradeDialog = () => {
  if (membershipLevels.value.length > 0) {
    selectedLevel.value = membershipLevels.value[1]
    upgradeDialogVisible.value = true
  }
}

const selectLevel = (level) => {
  selectedLevel.value = level
  upgradeDialogVisible.value = true
}

const confirmPurchase = async () => {
  try {
    await purchaseMembership({
      userId: userInfo.value.id,
      configId: selectedLevel.value.id
    })
    ElMessage.success('购买成功')
    upgradeDialogVisible.value = false
    loadMembership()
  } catch (error) {
    ElMessage.error('购买失败')
  }
}

onMounted(() => {
  loadMembership()
  loadMembershipLevels()
})
</script>

<style scoped>
.membership-center {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 16px;
}

.membership-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  color: white;
  margin-bottom: 16px;
}

.membership-card.level-BRONZE {
  background: linear-gradient(135deg, #cd7f32 0%, #b87333 100%);
}

.membership-card.level-SILVER {
  background: linear-gradient(135deg, #c0c0c0 0%, #a8a8a8 100%);
}

.membership-card.level-GOLD {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
}

.membership-card.level-PLATINUM {
  background: linear-gradient(135deg, #e5e4e2 0%, #b9b8b6 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.level-badge {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-icon {
  font-size: 32px;
}

.level-name {
  font-size: 20px;
  font-weight: bold;
}

.btn-upgrade {
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
}

.user-info {
  margin-bottom: 16px;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-id {
  font-size: 14px;
  opacity: 0.8;
}

.membership-info {
  display: flex;
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  opacity: 0.8;
}

.info-item .value {
  font-size: 14px;
  font-weight: bold;
}

.benefits-section,
.levels-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.benefit-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
}

.benefit-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.benefit-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #333;
}

.benefit-desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.benefit-quota {
  font-size: 12px;
  color: #667eea;
  font-weight: bold;
}

.levels-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.level-card {
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.level-card.active {
  border-color: #667eea;
  background: #f0f4ff;
}

.level-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.level-price {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 12px;
}

.level-benefits {
  margin-bottom: 12px;
}

.benefit-line {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.btn-select {
  width: 100%;
  background: #667eea;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.current-badge {
  text-align: center;
  color: #667eea;
  font-weight: bold;
  padding: 10px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.dialog-body {
  padding: 16px;
}

.selected-level {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.level-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.benefits-summary {
  padding: 16px;
  background: #f0f4ff;
  border-radius: 8px;
}

.benefits-summary .benefit-item {
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #e0e0e0;
}

.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  border: none;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-confirm {
  background: #667eea;
  color: white;
}
</style>
