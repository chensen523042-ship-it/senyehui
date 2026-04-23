package com.senyehui.security.init;

import com.senyehui.security.entity.SysUser;
import com.senyehui.security.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化 — 确保 admin 密码正确
 * 仅在开发环境首次启动时有用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查并修复 admin 用户密码
        SysUser admin = sysUserMapper.selectByUsername("admin", 1L);
        if (admin != null) {
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                String encoded = passwordEncoder.encode("admin123");
                admin.setPassword(encoded);
                sysUserMapper.updateById(admin);
                log.info("已重置 admin 用户密码为 admin123");
            } else {
                log.info("admin 用户密码已正确");
            }
        } else {
            log.warn("未找到 admin 用户，请检查 init.sql 是否已执行");
        }
    }
}
