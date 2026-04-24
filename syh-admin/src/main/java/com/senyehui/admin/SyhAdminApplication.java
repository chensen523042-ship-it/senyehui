package com.senyehui.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 森野汇 — 管理后台 API 启动类
 */
@SpringBootApplication(scanBasePackages = "com.senyehui")
@MapperScan(basePackages = {
        "com.senyehui.admin.mapper",
        "com.senyehui.tenant.mapper",
        "com.senyehui.security.mapper",
        "com.senyehui.event.mapper",
        "com.senyehui.registration.mapper",
        "com.senyehui.form.mapper",
        "com.senyehui.wechat.mapper",
        "com.senyehui.membership.mapper"
})
public class SyhAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyhAdminApplication.class, args);
        System.out.println("====================================");
        System.out.println("  森野汇 Admin API 启动成功！");
        System.out.println("  文档地址: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}
