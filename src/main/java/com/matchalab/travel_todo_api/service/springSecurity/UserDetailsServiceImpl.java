package com.matchalab.travel_todo_api.service.springSecurity;

import java.util.UUID;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID userId;
        try {
            userId = UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + username);
        }

        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        log.info("userAccount.id: " + userAccount.getId().toString() + "\n" + "userAccount.userRole: "
                + userAccount.getUserRole());

        final String PASSWORD = "";

        return User.builder()
                .username(userAccount.getId().toString())
                .password(PASSWORD)
                .authorities(userAccount.getUserRole())
                .build();
    }
}