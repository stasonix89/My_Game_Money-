package com.themoneygame.auth.application;

import com.themoneygame.auth.domain.Role;
import com.themoneygame.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final Long id;

    private final String username;
    private final String password;

    @Getter
    private final String displayName;

    private final boolean enabled;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String username,
                           String password,
                           String displayName,
                           boolean enabled,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImpl fromUser(User user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(Role::getName)                 // RoleType enum
                .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                .collect(Collectors.toSet());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getDisplayName(),
                user.isEnabled(),
                authorities
        );
    }

    // ------------------------------------------
    //  Новый метод, который требуется контроллерам
    // ------------------------------------------
    public User toUser() {
        User u = new User();
        u.setId(this.id);
        return u;
    }

    /**
     * Альтернативный метод, который уже был.
     * Пусть остаётся — он не мешает.
     */
    public User toUserRef() {
        User u = new User();
        u.setId(this.id);
        return u;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
