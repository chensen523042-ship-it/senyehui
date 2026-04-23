<template>
  <div class="admin-layout">
    <Sidebar />
    <div class="admin-layout__main" :style="{ marginLeft: sidebarWidth }">
      <Header />
      <div class="admin-layout__content">
        <router-view v-slot="{ Component }">
          <transition name="slide-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const appStore = useAppStore()
const sidebarWidth = computed(() =>
  appStore.sidebarCollapsed ? 'var(--syh-sidebar-collapsed-width)' : 'var(--syh-sidebar-width)'
)
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-layout__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left var(--syh-transition);
  min-width: 0;
}

.admin-layout__content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--syh-bg-page);
  height: calc(100vh - var(--syh-header-height));
}
</style>
