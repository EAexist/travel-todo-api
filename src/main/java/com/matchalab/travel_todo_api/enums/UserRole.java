package com.matchalab.travel_todo_api.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
