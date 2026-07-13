package org.example.petprojectweather.service;

import org.example.petprojectweather.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public AuthUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserWithRoles(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username:" + username));
        var authority = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                .toList();
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authority
        );
    }
}
