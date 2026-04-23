package com.senyehui.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.security.entity.SysUser;
import com.senyehui.security.mapper.SysUserMapper;
import com.senyehui.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    /** 默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 分页查询用户列表
     */
    public PageResult<SysUser> page(PageQuery pageQuery, String keyword, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> page = sysUserMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );

        // 脱敏：不返回密码
        page.getRecords().forEach(u -> u.setPassword(null));

        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 获取用户详情（含角色 ID 列表）
     */
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    /**
     * 获取用户角色 ID 列表
     */
    public List<Long> getUserRoleIds(Long userId) {
        return sysUserMapper.selectRoleIds(userId);
    }

    /**
     * 创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser create(SysUser user, List<Long> roleIds) {
        Long tenantId = TenantContext.getTenantId();

        // 校验用户名唯一性
        SysUser existing = sysUserMapper.selectByUsername(user.getUsername(), tenantId);
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 设置默认值
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        sysUserMapper.insert(user);

        // 关联角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                sysUserMapper.insertUserRole(user.getId(), roleId);
            }
        }

        log.info("创建用户: id={}, username={}", user.getId(), user.getUsername());
        user.setPassword(null);
        return user;
    }

    /**
     * 更新用户信息（不修改密码）
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUser user, List<Long> roleIds) {
        SysUser existing = getById(user.getId());

        // 保护超管
        if ("admin".equals(existing.getUsername()) && user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("不能禁用超级管理员");
        }

        // 如果修改了用户名，校验唯一性
        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(existing.getUsername())) {
            Long tenantId = TenantContext.getTenantId();
            SysUser dup = sysUserMapper.selectByUsername(user.getUsername(), tenantId);
            if (dup != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 不修改密码
        user.setPassword(null);
        sysUserMapper.updateById(user);

        // 重新关联角色
        if (roleIds != null) {
            sysUserMapper.deleteUserRoles(user.getId());
            for (Long roleId : roleIds) {
                sysUserMapper.insertUserRole(user.getId(), roleId);
            }
        }

        log.info("更新用户: id={}", user.getId());
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = getById(id);
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除超级管理员");
        }
        sysUserMapper.deleteUserRoles(id);
        sysUserMapper.deleteById(id);
        log.info("删除用户: id={}", id);
    }

    /**
     * 启用/禁用用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new BusinessException("不能禁用超级管理员");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(status);
        sysUserMapper.updateById(update);
        log.info("更新用户状态: id={}, status={}", id, status);
    }

    /**
     * 重置密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        SysUser update = new SysUser();
        update.setId(id);
        update.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        sysUserMapper.updateById(update);
        log.info("重置密码: id={}, username={}", id, user.getUsername());
    }
}
