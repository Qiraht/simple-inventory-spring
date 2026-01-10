package com.dibimbing.apiassignment.infrastructures.configuration;

import com.dibimbing.apiassignment.infrastructures.filter.JwtAuthFilter;
import com.dibimbing.apiassignment.infrastructures.handler.CustomAccessDeniedHandler;
import com.dibimbing.apiassignment.infrastructures.handler.UnAuthenticationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final UnAuthenticationHandler unAuthenticationHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        // Authentication Required
                        .requestMatchers(HttpMethod.POST, "/products")
                        .permitAll().anyRequest().authenticated()
                        .requestMatchers(HttpMethod.GET, "/products/**")
                        .permitAll().anyRequest().authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/products/**")
                        .permitAll().anyRequest().authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/products/**")
                        .permitAll().anyRequest().authenticated()
                        .requestMatchers(HttpMethod.PUT, "/products/**")
                        .permitAll().anyRequest().authenticated())
        .exceptionHandling(
                ex -> ex.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(unAuthenticationHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
