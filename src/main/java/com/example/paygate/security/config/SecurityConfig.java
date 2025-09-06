// Tạo file này theo hướng dẫn của ChatGPT để ứng phó tạm với Spring Security đã. Sẽ sửa đổi sau.
package com.example.paygate.security.config;

import com.example.paygate.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register", "/v1/auth/login").permitAll()
                        .requestMatchers("/actuator/health", "/error").permitAll() // Để vậy trước, bài 7, 8 sẽ nói kĩ hơn
                        .requestMatchers(HttpMethod.POST, "/v1/payments/**").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.GET, "/v1/payments/**").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.DELETE, "/v1/payments/**").hasAnyRole("ADMIN", "MERCHANT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
