package org.example.petprojectweather.service;

import org.example.petprojectweather.repository.UserRepository;
import org.example.petprojectweather.dto.RegisterUser;
import org.example.petprojectweather.dto.UserResponseDto;
import org.example.petprojectweather.entity.Role;
import org.example.petprojectweather.entity.User;
import org.example.petprojectweather.entity.enums.RoleEnum;
import org.example.petprojectweather.jwt.JwtHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtHelper jwtHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    @Transactional()
    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
    @Transactional(readOnly = true)
    public Optional<User> getUserWithRoles(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }
    //TODO сделать проверку на уже существующего пользователя
    @Transactional
    public UserResponseDto registerNewUser(RegisterUser registerUser) {
        User user = new User();
        user.setUsername(registerUser.username());
        user.setPassword(passwordEncoder.encode(registerUser.password()));
        Role role = new Role();
        role.setRole(RoleEnum.USER.name());
        role.setUser(user);
        user.addRole(role);
        return new UserResponseDto(userRepository.save(user).getUsername());
    }

    public String getAccessToken(Map<String, Object> claims, String username) {
        return jwtHelper.createTokenAccess(claims, username);
    }

    public String getRefreshToken(Map<String, Object> claims, String username) {
        return jwtHelper.createTokenRefresh(claims, username);
    }

    public long getRefreshTokenExpirationSeconds() {
        return jwtHelper.getRefreshTokenExpirationSeconds();
    }
}
