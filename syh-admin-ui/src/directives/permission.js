import { useUserStore } from '@/stores/user'

/**
 * v-permission 按钮级权限控制指令
 * 
 * 用法：
 *   <el-button v-permission="'event:create'">创建赛事</el-button>
 *   <el-button v-permission="['event:edit', 'event:delete']">编辑</el-button>
 */
export const permission = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const { value } = binding

    if (!value) return

    const permissions = userStore.permissions || []
    let hasPermission = false

    if (typeof value === 'string') {
      hasPermission = permissions.includes(value) || permissions.includes('*:*')
    } else if (Array.isArray(value)) {
      hasPermission = value.some(p => permissions.includes(p) || permissions.includes('*:*'))
    }

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

export default {
  install(app) {
    app.directive('permission', permission)
  }
}
