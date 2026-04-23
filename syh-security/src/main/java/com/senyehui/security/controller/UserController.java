package com.senyehui.security.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.security.entity.SysUser;
import com.senyehui.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 API（管理后台）
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public R<PageResult<SysUser>> page(PageQuery pageQuery,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer status) {
        return R.ok(userService.page(pageQuery, keyword, status));
    }

    /**
     * 获取用户详情（含角色 ID 列表）
     */
    @GetMapping("/{id}")
    public R<Map<String, Object>> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        List<Long> roleIds = userService.getUserRoleIds(id);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roleIds", roleIds);
        return R.ok(result);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public R<SysUser> create(@RequestBody Map<String, Object> body) {
        SysUser user = buildUser(body);
        @SuppressWarnings("unchecked")
        List<Long> roleIds = parseRoleIds(body.get("roleIds"));
        return R.ok(userService.create(user, roleIds));
    }

    /**
     * 更新用户
     */
    @PutMapping
    public R<Void> update(@RequestBody Map<String, Object> body) {
        SysUser user = buildUser(body);
        if (body.get("id") != null) {
            user.setId(Long.valueOf(body.get("id").toString()));
        }
        @SuppressWarnings("unchecked")
        List<Long> roleIds = parseRoleIds(body.get("roleIds"));
        userService.update(user, roleIds);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return R.ok();
    }

    // ========== 私有方法 ==========

    private SysUser buildUser(Map<String, Object> body) {
        SysUser user = new SysUser();
        if (body.get("username") != null) user.setUsername(body.get("username").toString());
        if (body.get("nickname") != null) user.setNickname(body.get("nickname").toString());
        if (body.get("phone") != null) user.setPhone(body.get("phone").toString());
        if (body.get("email") != null) user.setEmail(body.get("email").toString());
        if (body.get("status") != null) user.setStatus(Integer.valueOf(body.get("status").toString()));
        return user;
    }

    @SuppressWarnings("unchecked")
    private List<Long> parseRoleIds(Object raw) {
        if (raw instanceof List) {
            return ((List<Object>) raw).stream()
                    .map(o -> Long.valueOf(o.toString()))
                    .toList();
        }
        return null;
    }
}
