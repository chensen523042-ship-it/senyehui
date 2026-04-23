<template>
  <div class="header">
    <!-- 左侧：折叠按钮 + 面包屑 -->
    <div class="header__left">
      <el-icon class="header__trigger" @click="appStore.toggleSidebar">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="route.meta?.title && route.name !== 'Dashboard'">
          {{ route.meta.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧：主题切换 + 用户信息 -->
    <div class="header__right">
      <!-- 主题切换 -->
      <el-tooltip :content="appStore.isDark ? '切换亮色' : '切换暗色'" placement="bottom">
        <el-icon class="header__action" @click="appStore.toggleTheme">
          <Moon v-if="!appStore.isDark" />
          <Sunny v-else />
        </el-icon>
      </el-tooltip>

      <!-- 通知 -->
      <el-badge :value="3" :max="99" class="header__badge">
        <el-icon class="header__action"><Bell /></el-icon>
      </el-badge>

      <!-- 用户下拉 -->
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="header__user">
          <el-avatar :size="32" style="background: linear-gradient(135deg, var(--syh-primary), #8b5cf6);">
            {{ avatarText }}
          </el-avatar>
          <span class="header__username">{{ displayName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon> 个人中心
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const displayName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员')
const avatarText = computed(() => displayName.value.charAt(0))

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
    }).catch(() => {})
  }
}
</script>

<style scoped>
.header {
  height: var(--syh-header-height);
  background: var(--syh-header-bg);
  border-bottom: 1px solid var(--syh-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
  box-shadow: var(--syh-shadow-sm);
}

.header__left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header__trigger {
  font-size: 20px;
  cursor: pointer;
  color: var(--syh-text-regular);
  transition: color 0.2s;
}

.header__trigger:hover {
  color: var(--syh-primary);
}

.header__right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header__action {
  font-size: 18px;
  cursor: pointer;
  color: var(--syh-text-regular);
  transition: color 0.2s;
}

.header__action:hover {
  color: var(--syh-primary);
}

.header__badge {
  display: flex;
  align-items: center;
}

.header__user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--syh-radius);
  transition: background 0.2s;
}

.header__user:hover {
  background: var(--syh-primary-bg);
}

.header__username {
  font-size: 14px;
  font-weight: 500;
  color: var(--syh-text-primary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
