package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    Optional<UserAccount> findByKakaoId(String kakaoId);

    // Optional<UserAccount> findByKakaoIdToken(String kakaoIdToken);

    Optional<UserAccount> findByGoogleId(String googleId);

    // Optional<UserAccount> findByGoogleIdToken(String googleIdToken);
}
