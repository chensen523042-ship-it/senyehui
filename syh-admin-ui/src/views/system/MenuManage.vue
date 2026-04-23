<template>
  <div>
    <el-table
      :data="tree"
      row-key="id"
      border
      :tree-props="{ children: 'children' }"
      default-expand-all
      style="width:100%"
    >
      <el-table-column prop="name" label="菜单名称" min-width="180" />
      <el-table-column label="类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.type === 1" type="primary" size="small">目录</el-tag>
          <el-tag v-else-if="row.type === 2" type="success" size="small">菜单</el-tag>
          <el-tag v-else type="info" size="small">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="icon" label="图标" width="100" align="center" />
      <el-table-column prop="path" label="路由路径" min-width="160" />
      <el-table-column prop="component" label="组件" min-width="180" show-overflow-tooltip />
      <el-table-column prop="perms" label="权限标识" min-width="180" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const tree = ref([])

function buildTree(flat) {
  const map = {}
  flat.forEach(n => { map[n.id] = { ...n, children: [] } })
  const roots = []
  flat.forEach(n => {
    if (n.parentId === 0 || n.parentId === '0') {
      roots.push(map[n.id])
    } else if (map[n.parentId]) {
      map[n.parentId].children.push(map[n.id])
    }
  })
  return roots
}

onMounted(async () => {
  const res = await request.get('/api/system/role/permissions/all')
  tree.value = buildTree(res.data || [])
})
</script>
