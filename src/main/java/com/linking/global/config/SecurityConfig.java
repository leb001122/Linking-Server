package com.linking.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll(); // 해당 패턴의 url의 접근을 인증없이 허용한다. -> 무슨인증인데 그게;;
//                .anyRequest().authenticated(); // 모든 리소스가 인증을 해야 접근이 허용된다.
//                .formLogin().disable()
//                .headers().frameOptions().disable();

        return httpSecurity.build();
    }
}
