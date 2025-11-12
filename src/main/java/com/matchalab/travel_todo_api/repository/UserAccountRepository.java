package com.matchalab.travel_todo_api.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByKakaoId(String kakaoId);

    // Optional<UserAccount> findByKakaoIdToken(String kakaoIdToken);

    Optional<UserAccount> findByGoogleId(String googleId);

    // Optional<UserAccount> findByGoogleIdToken(String googleIdToken);
}
