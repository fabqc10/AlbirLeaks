package com.fabdev.AlbirLeaks.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/jobs").permitAll())
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}

// http
//         .authorizeRequests(authorizeRequests ->
//         authorizeRequests
//         .requestMatchers("/", "/login**", "/error**").permitAll()
//         .requestMatchers("/jobs", "/jobs/**").permitAll()
//         .requestMatchers(HttpMethod.POST, "/jobs").authenticated()
//         .requestMatchers(HttpMethod.DELETE, "/jobs/**").authenticated()
//         .anyRequest().authenticated()
//         )
//         .oauth2Login(oauth2Login ->
//         oauth2Login
//         .loginPage("/login")
//         )
//         .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
//         .exceptionHandling(exceptionHandling ->
//         exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//         );
