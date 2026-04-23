<template>
  <router-view v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
  <!-- 底部导航（登录页隐藏） -->
  <van-tabbar v-if="!hideTabbar" v-model="activeTab" route fixed placeholder>
    <van-tabbar-item to="/" icon="fire-o">赛事</van-tabbar-item>
    <van-tabbar-item to="/my" icon="orders-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref(0)
const hideTabbar = computed(() => route.meta?.hideTabbar === true)
</script>
