package com.matchalab.travel_todo_api.model.UserAccount;

import javax.annotation.Nullable;

import lombok.Builder;

@Builder
public record GoogleProfile(
        String id,
        String name,
        @Nullable String email,
        @Nullable String photo,
        @Nullable String familyName,
        @Nullable String givenName) {
};
