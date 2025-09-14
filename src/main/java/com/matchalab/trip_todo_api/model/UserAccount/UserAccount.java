package com.matchalab.trip_todo_api.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.GoogleUserDTO;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nullable
    private String nickname;

    private String kakaoId;
    private String googleId;

    @JdbcTypeCode(SqlTypes.JSON)
    private KakaoProfile kakaoProfile;

    @JdbcTypeCode(SqlTypes.JSON)
    private GoogleProfile googleProfile;

    // @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval
    // = true)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Trip> trip = new ArrayList<Trip>();

    public UserAccount() {
        this.nickname = "guest";
        this.trip = new ArrayList<Trip>();
    }

    public UserAccount(String kakaoId, KakaoProfile kakaoProfile) {
        this();
        this.kakaoId = kakaoId;
        this.kakaoProfile = kakaoProfile;
    }

    public UserAccount(GoogleProfile googleUserDTO) {
        this();
        this.googleId = googleUserDTO.id();
        this.googleProfile = googleUserDTO;
    }

    public UserAccount(GoogleUserDTO googleUserDTO) {
        this();
        this.googleId = googleUserDTO.idToken();
        this.googleProfile = googleUserDTO.user();
    }
}
