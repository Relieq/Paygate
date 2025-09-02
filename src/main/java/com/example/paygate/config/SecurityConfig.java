// Tạo file này theo hướng dẫn của ChatGPT để ứng phó tạm với Spring Security đã. Sẽ sửa đổi sau.
package com.example.paygate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())                 // API thuần JSON: tắt CSRF
                .formLogin(form -> form.disable())            // tắt trang /login mặc định
                .httpBasic(basic -> basic.disable())          // không bật Basic popup
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register", "/v1/auth/login").permitAll()
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        .anyRequest().permitAll()                   // DEV: mở hết; (Bài 4 đổi thành authenticated)
                )
                .build();
    }
}
