package com.nunclear.escritores.config;

import com.nunclear.escritores.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/verify-email",
                                "/users/*",
                                "/users/*/public-profile",
                                "/users/*/stories",
                                "/stories",
                                "/stories/*",
                                "/stories/slug/*",
                                "/stories/search",
                                "/stories/user/*",
                                "/chapters/*",
                                "/chapters/story/*",
                                "/chapters/story/*/published",
                                "/chapters/search",
                                "/arcs/*",
                                "/arcs/story/*",
                                "/volumes/*",
                                "/volumes/story/*",
                                "/characters/*",
                                "/characters/story/*",
                                "/characters/search",
                                "/skills/*",
                                "/skills/story/*",
                                "/skills/search",
                                "/character-skills/character/*",
                                "/character-skills/skill/*",
                                "/events/*",
                                "/events/story/*",
                                "/events/chapter/*",
                                "/events/search",
                                "/items/*",
                                "/items/story/*"
                        ).permitAll()
                        .requestMatchers("/ideas/**").authenticated()
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}