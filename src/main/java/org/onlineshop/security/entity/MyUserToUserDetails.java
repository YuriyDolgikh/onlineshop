package org.onlineshop.security.entity;

import lombok.RequiredArgsConstructor;
import org.onlineshop.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class MyUserToUserDetails implements UserDetails {

    private final User user;

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getHashPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().name().equals("CONFIRMED");
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getStatus().name().equals("DELETED");
    }

    // Methods, written below, are not used in this project and made for keep the structure of the project

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
