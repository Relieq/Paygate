package com.example.paygate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
public class SecurityBeans {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // Tham số strength của encoder trên (work factor - quyết định số lần hash 2^strength) mặc định là 10, có thể tăng 12 cho production (test chậm hơn).
    }
}
