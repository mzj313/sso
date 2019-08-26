package com.ssyt.app.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 被sso保护的子应用
 * @author huangshuqing
 *
 */

@SpringBootApplication
@EnableWebMvc
public class PermissionManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionManagementApplication.class, args);
    }

}
