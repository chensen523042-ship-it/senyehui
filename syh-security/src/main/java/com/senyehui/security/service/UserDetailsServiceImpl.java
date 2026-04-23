package com.senyehui.security.service;

import com.senyehui.security.entity.SysUser;
import com.senyehui.security.mapper.SysUserMapper;
import com.senyehui.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务 — Spring Security UserDetailsService 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new UsernameNotFoundException("未设置租户上下文");
        }

        SysUser sysUser = sysUserMapper.selectByUsername(username, tenantId);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        if (sysUser.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 加载权限
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 角色
        List<String> roleCodes = sysUserMapper.selectRoleCodes(sysUser.getId());
        roleCodes.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

        // 权限标识
        List<String> permissions = sysUserMapper.selectPermissions(sysUser.getId());
        permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));

        return new User(sysUser.getUsername(), sysUser.getPassword(), authorities);
    }
}
