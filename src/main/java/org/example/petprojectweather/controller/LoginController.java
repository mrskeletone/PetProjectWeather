package org.example.petprojectweather.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.petprojectweather.dto.LoginDto;
import org.example.petprojectweather.dto.RegisterUser;
import org.example.petprojectweather.dto.TokenResponse;
import org.example.petprojectweather.dto.UserResponseDto;
import org.example.petprojectweather.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class LoginController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public LoginController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping(value = "/register",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> registerNewUser(@RequestBody RegisterUser registerUser){
       return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerNewUser(registerUser));
    }
    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(),loginDto.password())
        );
        String refreshToken= userService.getRefreshToken(Map.of(),loginDto.username());
        Cookie cookie=new Cookie("refresh_toker",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int)userService.getRefreshTokenExpirationSeconds());
        cookie.setAttribute("SameSite","Strict");
        response.addCookie(cookie);
        return ResponseEntity.ok(new TokenResponse(userService.getAccessToken(Map.of(),loginDto.username())));
    }
}
