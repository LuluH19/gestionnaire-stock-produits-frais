package com.stock.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/api/**").permitAll()
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions().disable()) // nécessaire pour H2
            .httpBasic(httpBasic -> {}); // empêche la redirection vers /login
        return http.build();
    }
}
