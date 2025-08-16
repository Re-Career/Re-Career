package com.recareer.backend.config;

import com.recareer.backend.auth.service.CustomOAuth2UserService;
import com.recareer.backend.auth.handler.OAuth2AuthenticationFailureHandler;
import com.recareer.backend.auth.handler.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**", "/profile/**", "/user/**", "/personality-tags/**", "/mentors/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/positions/**", "/news/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.contains("CareerApp-WebView")) {
                    // CareerApp-WebView인 경우 Origin 헤더에서 동적으로 설정
                    String origin = request.getHeader("Origin");
                    if (origin != null) {
                        configuration.setAllowedOrigins(Arrays.asList(origin));
                    } else {
                        configuration.setAllowedOrigins(Arrays.asList("*"));
                    }
                } else {
                    // 일반적인 경우 설정된 allowed-origins 사용
                    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                }
                
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L); // preflight 요청 캐시 시간 (1시간)
                
                return configuration;
            }
        };
    }
}