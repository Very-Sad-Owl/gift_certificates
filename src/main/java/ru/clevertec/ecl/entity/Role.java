package ru.clevertec.ecl.entity;

public enum Role/* implements GrantedAuthority*/ {

    ADMIN;

    Role() {
    }

//    @Override
    public String getAuthority() {
        return name();
    }
}
