package com.example.DevPlayground.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final GrantedAuthority authority;

    Role(String authority) {
        this.authority = () -> authority;
    }
}
