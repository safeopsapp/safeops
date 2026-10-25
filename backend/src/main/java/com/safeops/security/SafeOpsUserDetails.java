package com.safeops.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SafeOpsUserDetails implements UserDetails {

    private final SafeOpsPrincipal principal;
    private final Set<GrantedAuthority> authorities;

    public SafeOpsUserDetails(SafeOpsPrincipal principal) {
        this.principal = principal;
        this.authorities = principal.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
        // Also add permission-based authorities
        principal.getPermissions().stream()
            .map(perm -> new SimpleGrantedAuthority(perm.name()))
            .forEach(authorities::add);
    }

    public SafeOpsPrincipal getPrincipal() { return principal; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return null; }

    @Override
    public String getUsername() { return principal.getFirebaseUid(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
