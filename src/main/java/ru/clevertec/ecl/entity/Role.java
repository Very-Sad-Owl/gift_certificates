package ru.clevertec.ecl.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role/* implements GrantedAuthority*/ {

    ADMIN;

    Role() {
    }

//    @Override
    public String getAuthority() {
        return name();
    }
}
