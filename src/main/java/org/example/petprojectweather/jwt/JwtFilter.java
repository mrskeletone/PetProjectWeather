package org.example.petprojectweather.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.petprojectweather.service.AuthUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;


@Component
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    private final JwtHelper jwtHelper;
    private final AuthUserDetailsService authUserDetailsService;

    public JwtFilter(JwtHelper jwtHelper, AuthUserDetailsService authUserDetailsService) {
        this.jwtHelper = jwtHelper;
        this.authUserDetailsService = authUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String jwt = null, username = null;
        if (Objects.nonNull(authorizationHeader) &&
                authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtHelper.extractUsername(jwt);
        }
        if (Objects.nonNull(username) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.authUserDetailsService.loadUserByUsername(username);
            boolean isTokenValidated=false;
            try {
                isTokenValidated = this.jwtHelper.validationToken(jwt, userDetails);
            } catch (Exception e){
                logger.warn("JWT validation error: {}",e);
            }
            if (isTokenValidated) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}
