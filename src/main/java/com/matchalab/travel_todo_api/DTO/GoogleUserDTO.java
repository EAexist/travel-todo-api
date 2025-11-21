package com.matchalab.travel_todo_api.DTO;

import com.matchalab.travel_todo_api.model.UserAccount.GoogleProfile;

import lombok.Builder;

@Builder
public record GoogleUserDTO(
        GoogleProfile user,
        String idToken
// scopes: string[];
) {
}