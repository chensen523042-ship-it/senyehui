<template>
  <div class="sidebar" :class="{ 'sidebar--collapsed': appStore.sidebarCollapsed }">
    <!-- Logo -->
    <div class="sidebar__logo">
      <div class="sidebar__logo-icon">
        <el-icon :size="24"><Trophy /></el-icon>
      </div>
      <transition name="fade">
        <span v-show="!appStore.sidebarCollapsed" class="sidebar__logo-text">森野汇</span>
      </transition>
    </div>

    <!-- 导航菜单 -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="var(--syh-sidebar-text)"
        active-text-color="#ffffff"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="'/' + route.path">
            <el-icon><component :is="route.meta?.icon || 'Document'" /></el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

// 过滤出要显示在菜单中的路由
const menuRoutes = computed(() => {
  const mainRoute = router.options.routes.find(r => r.path === '/')
  if (!mainRoute || !mainRoute.children) return []
  return mainRoute.children.filter(child => !child.meta?.hidden)
})

// 当前激活菜单
const activeMenu = computed(() => {
  return route.meta?.activeMenu || route.path
})
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--syh-sidebar-width);
  background: var(--syh-sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width var(--syh-transition);
  z-index: 1000;
  overflow: hidden;
}

.sidebar--collapsed {
  width: var(--syh-sidebar-collapsed-width);
}

/* Logo */
.sidebar__logo {
  height: var(--syh-header-height);
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.sidebar__logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--syh-primary), #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.sidebar__logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  white-space: nowrap;
  letter-spacing: 1px;
}

/* 菜单样式覆写 */
.sidebar :deep(.el-menu) {
  border-right: none;
  padding: 8px;
}

.sidebar :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin-bottom: 2px;
  border-radius: var(--syh-radius-sm);
  transition: all 0.2s;
}

.sidebar :deep(.el-menu-item:hover) {
  background: var(--syh-sidebar-hover) !important;
}

.sidebar :deep(.el-menu-item.is-active) {
  background: var(--syh-primary) !important;
  color: #ffffff !important;
}

.sidebar :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

.sidebar--collapsed :deep(.el-menu) {
  padding: 8px 4px;
}

.sidebar--collapsed .sidebar__logo {
  justify-content: center;
  padding: 0;
}
</style>
