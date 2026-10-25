package com.safeops.security;

import com.safeops.auth.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final FirebaseTokenVerifier tokenVerifier;
    private final AuthService authService;

    public SecurityConfig(FirebaseTokenVerifier tokenVerifier, AuthService authService) {
        this.tokenVerifier = tokenVerifier;
        this.authService = authService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers(
                    "/api/v1/auth/referral/**",
                    "/api/v1/auth/register-admin",
                    "/api/v1/auth/register-staff",
                    "/api/v1/auth/sync",
                    "/api/v1/payments/webhook"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new FirebaseAuthenticationFilter(tokenVerifier, authService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
