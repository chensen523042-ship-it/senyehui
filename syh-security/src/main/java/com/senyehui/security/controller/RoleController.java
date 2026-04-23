package com.senyehui.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.common.model.R;
import com.senyehui.security.entity.SysPermission;
import com.senyehui.security.entity.SysRole;
import com.senyehui.security.entity.SysRolePermission;
import com.senyehui.security.mapper.SysPermissionMapper;
import com.senyehui.security.mapper.SysRoleMapper;
import com.senyehui.security.mapper.SysRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理 API（管理后台）
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;

    /** 获取所有启用的角色列表 */
    @GetMapping("/list")
    public R<List<SysRole>> listAll() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getSortOrder);
        return R.ok(sysRoleMapper.selectList(wrapper));
    }

    /** 获取角色详情（含权限 ID 列表） */
    @GetMapping("/{id}")
    public R<Map<String, Object>> getById(@PathVariable Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(id);
        return R.ok(Map.of("role", role, "permissionIds", permissionIds));
    }

    /** 创建角色 */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<SysRole> create(@RequestBody SysRole role) {
        if (role.getStatus() == null) role.setStatus(1);
        if (role.getSortOrder() == null) role.setSortOrder(0);
        sysRoleMapper.insert(role);
        return R.ok(role);
    }

    /** 更新角色 */
    @PutMapping
    public R<Void> update(@RequestBody SysRole role) {
        sysRoleMapper.updateById(role);
        return R.ok();
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> delete(@PathVariable Long id) {
        rolePermissionMapper.deleteByRoleId(id);
        sysRoleMapper.deleteById(id);
        return R.ok();
    }

    /** 分配权限 */
    @PutMapping("/{id}/permissions")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(id);
        for (Long permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(id);
            rp.setPermissionId(permId);
            rolePermissionMapper.insert(rp);
        }
        return R.ok();
    }

    /** 获取所有权限列表（供分配权限使用） */
    @GetMapping("/permissions/all")
    public R<List<SysPermission>> listAllPermissions() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysPermission::getSortOrder);
        return R.ok(permissionMapper.selectList(wrapper));
    }
}
