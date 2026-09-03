package com.matchalab.travel_todo_api.config.security;

import com.matchalab.travel_todo_api.model.UserAccount.GoogleProfile;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("no-security")
public class NoWebSecurityConfig {

    private final UserAccountService userAccountService;
    private final UserDetailsService userDetailsService;

    public NoWebSecurityConfig(UserAccountService userAccountService, UserDetailsService userDetailsService) {
        this.userAccountService = userAccountService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(new DevMockAuthFilter(userAccountService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static class DevMockAuthFilter extends OncePerRequestFilter {

        private final UserAccountService userAccountService;
        private final UserDetailsService userDetailsService;

        private DevMockAuthFilter(UserAccountService userAccountService, UserDetailsService userDetailsService) {
            this.userAccountService = userAccountService;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                GoogleProfile googleProfile =
                        GoogleProfile.builder().id("subjectr").email("email").build();

                UserAccount userAccount = userAccountService.findOrCreateAdminAccount(googleProfile);
                UserDetails user = userDetailsService.loadUserByUsername(userAccount.getId().toString());
                var mockUser = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword(), user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(mockUser);
            }

            chain.doFilter(request, response);
        }
    }
}