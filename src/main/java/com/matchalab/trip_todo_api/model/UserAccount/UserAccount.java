package com.matchalab.trip_todo_api.model.UserAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.DTO.GoogleUserDTO;
import com.matchalab.trip_todo_api.enums.UserRole;
import com.matchalab.trip_todo_api.model.Trip;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole userRole = UserRole.USER;

    @Nullable
    private String nickname;

    private String kakaoId;
    private String googleId;

    @JdbcTypeCode(SqlTypes.JSON)
    private KakaoProfile kakaoProfile;

    @JdbcTypeCode(SqlTypes.JSON)
    private GoogleProfile googleProfile;

    @Nullable
    private UUID activeTripId;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Trip> trips = new ArrayList<Trip>();

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

    public Trip addTrip(Trip trip) {
        // Add
        this.trips.add(trip);
        trip.setUserAccount(this);
        // Always set newly added trip as activeTrip
        this.setActiveTripId(trip.getId());
        return trip;
    }

    public void removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setUserAccount(null);
    }
}
