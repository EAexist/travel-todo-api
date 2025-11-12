package com.matchalab.travel_todo_api.config.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.matchalab.travel_todo_api.model.UserAccount.GoogleProfile;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.service.UserAccountService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AdminLoginFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;
    private final UserDetailsService userDetailsService;

    private final String adminAuthPath;

    @Value("${app.google.web-client-id}")
    private String googleWebClientId;

    @Value("#{'${app.security.admin-emails}'.split(',')}")
    private List<String> adminEmails;

    @Override
    // @Transactional
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (existingAuth == null && request.getRequestURI().equals(adminAuthPath)
                && request.getMethod().equals("POST")) {

            try {
                String idToken = request.getParameter("idToken");
                GoogleIdToken verifiedIdToken = verifyGoogleIdToken(idToken);

                if (verifiedIdToken == null) {
                    log.warn("Failed to verify google id token");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return;
                }

                GoogleIdToken.Payload payload = verifiedIdToken.getPayload();

                if (!adminEmails.contains(payload.getEmail())) {
                    log.warn("Email is not registered as administrator");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                GoogleProfile googleProfile = GoogleProfile.builder().id(payload.getSubject()).email(payload.getEmail())
                        .build();

                UserAccount userAccount = userAccountService.findOrCreateAdminAccount(googleProfile);
                UserDetails user = userDetailsService.loadUserByUsername(userAccount.getId().toString());

                Authentication authenticated = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword(), user.getAuthorities());

                log.info(String.format("Authentication Success\nusername:%s\nauthorities:%s", user.getUsername(),
                        user.getAuthorities().toString()));

                SecurityContextHolder.getContext().setAuthentication(authenticated);

            } catch (Exception e) {
                // logger.error("Error creating anonymous user and issuing cookie: " +
                // e.getMessage(), e);
                // Handle failure gracefully
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("{\"error\": \"Failed to create anonymous user.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    protected GoogleIdToken verifyGoogleIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleWebClientId))
                    .build();
            return googleIdTokenVerifier.verify(idToken);
        } catch (GeneralSecurityException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
