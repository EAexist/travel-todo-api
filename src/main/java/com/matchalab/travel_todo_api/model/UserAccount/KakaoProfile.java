package com.matchalab.travel_todo_api.model.UserAccount;

import lombok.Builder;

@Builder
public record KakaoProfile(
        String id,
        String email,
        String name,
        String nickname,
        String profileImageUrl,
        String thumbnailImageUrl,
        String phoneNumber,
        String ageRange,
        String birthday,
        String birthdayType,
        String birthyear,
        String gender,
        Boolean isKorean) {
};
