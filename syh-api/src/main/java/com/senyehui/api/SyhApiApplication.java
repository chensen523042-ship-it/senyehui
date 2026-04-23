package com.senyehui.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 森野汇 — C端 API 启动类
 */
@SpringBootApplication(scanBasePackages = "com.senyehui")
@MapperScan(basePackages = {
        "com.senyehui.tenant.mapper",
        "com.senyehui.security.mapper",
        "com.senyehui.event.mapper",
        "com.senyehui.registration.mapper",
        "com.senyehui.form.mapper",
        "com.senyehui.wechat.mapper"
})
public class SyhApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyhApiApplication.class, args);
        System.out.println("====================================");
        System.out.println("  森野汇 C端 API 启动成功！");
        System.out.println("  端口: 8081");
        System.out.println("====================================");
    }
}
