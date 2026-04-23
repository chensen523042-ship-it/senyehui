import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const isDark = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function toggleTheme() {
    isDark.value = !isDark.value
    if (isDark.value) {
      document.documentElement.classList.add('dark')
      localStorage.setItem('syh_theme', 'dark')
    } else {
      document.documentElement.classList.remove('dark')
      localStorage.setItem('syh_theme', 'light')
    }
  }

  // 初始化主题
  function initTheme() {
    const saved = localStorage.getItem('syh_theme')
    if (saved === 'dark') {
      isDark.value = true
      document.documentElement.classList.add('dark')
    }
  }

  return { sidebarCollapsed, isDark, toggleSidebar, toggleTheme, initTheme }
})
