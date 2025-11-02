package com.matchalab.trip_todo_api.DTO;

import com.matchalab.trip_todo_api.model.UserAccount.GoogleProfile;

import lombok.Builder;

@Builder
public record GoogleUserDTO(
        GoogleProfile user,
        String idToken
// scopes: string[];
) {
}