import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'

NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台', icon: 'Odometer' }
      },
      {
        path: 'event',
        name: 'EventList',
        component: () => import('@/views/event/EventList.vue'),
        meta: { title: '赛事管理', icon: 'Trophy' }
      },
      {
        path: 'event/create',
        name: 'EventCreate',
        component: () => import('@/views/event/EventForm.vue'),
        meta: { title: '创建赛事', hidden: true, activeMenu: '/event' }
      },
      {
        path: 'event/edit/:id',
        name: 'EventEdit',
        component: () => import('@/views/event/EventForm.vue'),
        meta: { title: '编辑赛事', hidden: true, activeMenu: '/event' }
      },
      {
        path: 'registration',
        name: 'RegistrationList',
        component: () => import('@/views/registration/RegistrationList.vue'),
        meta: { title: '报名管理', icon: 'DocumentChecked' }
      },
      {
        path: 'form',
        name: 'FormDesigner',
        component: () => import('@/views/form/FormDesigner.vue'),
        meta: { title: '表单管理', icon: 'EditPen' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'tenant/list',
        name: 'TenantList',
        component: () => import('@/views/tenant/TenantList.vue'),
        meta: { title: '租户管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'tenant/package',
        name: 'PackageManage',
        component: () => import('@/views/tenant/PackageManage.vue'),
        meta: { title: '套餐管理', icon: 'GoodsFilled' }
      },
      {
        path: 'payment/list',
        name: 'PaymentList',
        component: () => import('@/views/payment/PaymentList.vue'),
        meta: { title: '支付管理', icon: 'CreditCard' }
      },
      {
        path: 'event/category',
        name: 'CategoryManage',
        component: () => import('@/views/event/CategoryManage.vue'),
        meta: { title: '赛事分类', icon: 'Collection' }
      },
      {
        path: 'registration/checkin',
        name: 'CheckIn',
        component: () => import('@/views/registration/CheckIn.vue'),
        meta: { title: '签到管理', icon: 'CircleCheck' }
      },
      {
        path: 'system/menu',
        name: 'MenuManage',
        component: () => import('@/views/system/MenuManage.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { title: '数据统计', icon: 'DataLine' }
      },
      {
        path: 'membership/list',
        name: 'MembershipList',
        component: () => import('@/views/membership/MembershipList.vue'),
        meta: { title: '会员管理', icon: 'Postcard' }
      },
      {
        path: 'membership/config',
        name: 'MembershipConfig',
        component: () => import('@/views/membership/ConfigManage.vue'),
        meta: { title: '会员配置', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

// 路由守卫
const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || ''} - 森野汇`

  const token = getToken()
  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      // 有 token 但还没加载用户信息时，自动获取权限
      const { useUserStore } = await import('@/stores/user')
      const userStore = useUserStore()
      if (!userStore.userInfo) {
        await userStore.fetchUserInfo()
      }
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
