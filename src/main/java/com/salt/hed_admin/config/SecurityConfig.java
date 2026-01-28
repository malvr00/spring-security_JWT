package com.salt.hed_admin.config;

import com.salt.hed_admin.common.filter.JwtAuthFilter;
import com.salt.hed_admin.common.handler.CustomAccessDeniedHandler;
import com.salt.hed_admin.common.handler.CustomAuthenticationEntryPoint;
import com.salt.hed_admin.common.handler.TokenProvider;
import com.salt.hed_admin.feature.user.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.salt.hed_admin.common.Const.ACTIVE.*;
import static com.salt.hed_admin.common.Router.ACTUATOR.HEALTH_CHECK;
import static com.salt.hed_admin.common.Router.API_VERSION.V1_BASE_PATH;
import static com.salt.hed_admin.common.Router.BASE_URL.ADMIN;
import static com.salt.hed_admin.common.Router.CERTIFIED_URI.LOGIN;
import static com.salt.hed_admin.common.Router.CERTIFIED_URI.SIGN;
import static com.salt.hed_admin.common.Router.ERROR_URI.ERROR;
import static com.salt.hed_admin.common.Router.SWAGGER_URI.*;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final Environment env;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            V1_BASE_PATH + ADMIN + SIGN,
            V1_BASE_PATH + ADMIN + LOGIN,
            ERROR, AUTH_1, AUTH_2, AUTH_3, HEALTH_CHECK
    };

    /**
     * CORS Method
     */
    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();

            // 허용할 헤더
            config.setAllowedHeaders(List.of("*"));

            // 허용할 메소드
            config.setAllowedMethods(List.of(
                    "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

            // 허용할 URL
            List<String> allowOrigins = new ArrayList<>();
            if (Objects.equals(env.getProperty(ACTIVE_BASE), ACTIVE_DEV)) {
                // DEV
                allowOrigins.add("http://localhost:3000");
                allowOrigins.add("http://localhost:3001");
                allowOrigins.add("http://localhost:3002");
            } else if (Objects.equals(env.getProperty(ACTIVE_BASE), ACTIVE_LIVE)) {
                // LIVE
            }

            config.setAllowedOrigins(allowOrigins);
            config.setAllowCredentials(true);

            return config;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF, CORS, CSP, XSS
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .headers(headersConfigurer -> {
                    headersConfigurer.xssProtection(
                            xXssConfig -> xXssConfig.headerValue(
                                    XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                    ).contentSecurityPolicy(
                            cps -> cps.policyDirectives("script-src 'self'")
                    );
                })

                // session off
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // FormLogin, BasicHttp 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // JWTAuthFilter, UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        new JwtAuthFilter(userService, tokenProvider), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))

                // Method 제어
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }
}
