package com.matchalab.trip_todo_api.config.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.service.UserAccountService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnonymousUserLoginFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;
    private final UserDetailsService userDetailsService;

    private final String anonymousAuthPath;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (existingAuth == null && request.getRequestURI().equals(anonymousAuthPath)
                && request.getMethod().equals("POST")) {

            try {
                UserAccount newUserAccount = userAccountService.createNewUserAccount();
                UserDetails user = userDetailsService.loadUserByUsername(newUserAccount.getId().toString());

                Authentication authenticated = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword(), user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticated);

            } catch (Exception e) {
                logger.error("Error creating anonymous user and issuing cookie: " + e.getMessage(), e);
                // Handle failure gracefully
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("{\"error\": \"Failed to create anonymous user.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
