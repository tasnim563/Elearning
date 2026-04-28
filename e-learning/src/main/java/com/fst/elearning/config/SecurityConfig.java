package com.fst.elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/**",
                                "/catalogue/**",
                                "/cours/**",
                                "/progression/**",
                        "/quiz/**",
                        "/admin/login",
                        "/admin/assets/**",
                        "/h2-console/**",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/js/app/**",
                        "/images/**"
                        ).permitAll() // accès libre
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/manager", "/manager/**").hasRole("FORMATEUR")
                        .requestMatchers("/formateur/**").hasRole("FORMATEUR")
                        .requestMatchers("/apprenant/**").hasRole("APPRENANT")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            String target = "/progression";
                            if (hasRole(authentication, "ADMIN")) {
                                target = "/admin";
                            } else if (hasRole(authentication, "FORMATEUR")) {
                                target = "/manager";
                            } else if (hasRole(authentication, "APPRENANT")) {
                                target = "/progression";
                            }
                            response.sendRedirect(target);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}
