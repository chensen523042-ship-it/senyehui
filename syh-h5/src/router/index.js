import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'EventList',
    component: () => import('@/views/EventList.vue'),
    meta: { title: '赛事活动' }
  },
  {
    path: '/event/:id',
    name: 'EventDetail',
    component: () => import('@/views/EventDetail.vue'),
    meta: { title: '赛事详情' }
  },
  {
    path: '/register/:id',
    name: 'Register',
    component: () => import('@/views/RegisterForm.vue'),
    meta: { title: '报名', requiresAuth: true }
  },
  {
    path: '/pay/:id',
    name: 'Payment',
    component: () => import('@/views/PaymentView.vue'),
    meta: { title: '订单支付', requiresAuth: true, hideTabbar: true }
  },
  {
    path: '/my',
    name: 'MyRegistrations',
    component: () => import('@/views/MyRegistrations.vue'),
    meta: { title: '我的报名', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录', hideTabbar: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '森野汇'} - 森野汇`
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('syh_h5_token')
    if (!token) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }
})

export default router
