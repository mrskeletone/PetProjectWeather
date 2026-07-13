package org.example.petprojectweather.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.petprojectweather.config.JwtProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {
    private final JwtProperty jwtProperty;

    public JwtHelper(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
    }

    public String createTokenAccess(Map<String,Object> claims, String subject){
        Date expiryDate = getDateByExp(jwtProperty.getExp_access());
        byte[] keyBytes =Base64.getDecoder().decode(jwtProperty.getSecretCode());
        SecretKey key= Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .signWith(key)
                .compact();

    }
    public String createTokenRefresh(Map<String,Object> claims, String subject){
        Date expiryDate = getDateByExp(jwtProperty.getExp_refresh());
        byte[] keyBytes =Base64.getDecoder().decode(jwtProperty.getSecretCode());
        SecretKey key= Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .signWith(key)
                .compact();

    }
    private Date getDateByExp(long exp){
        return Date.from(Instant.ofEpochMilli(System.currentTimeMillis()+exp));
    }
    private Jws<Claims> extractClaims(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperty.getSecretCode())))
                .build()
                .parseSignedClaims(token);
    }
    public <T> T extractClaimsBody(String token, Function<Claims,T> claimsTFunction){
        Jws<Claims> claimsJws=extractClaims(token);
        return  claimsTFunction.apply(claimsJws.getPayload());
    }
    public <T> T extractClaimsHeader(String token ,Function<JwsHeader,T> claimsTFunction){
        Jws<Claims> claimsJws =extractClaims(token);
        return claimsTFunction.apply(claimsJws.getHeader());
    }
    public Date extractClaimsExpiry(String token){
       return extractClaimsBody(token,Claims::getExpiration);
    }
    public String extractUsername(String token){
        return extractClaimsBody(token,Claims::getSubject);
    }
    public boolean isTokenExpired(String token){
        return extractClaimsExpiry(token).before(new Date());
    }
    public boolean validationToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    public long getRefreshTokenExpirationSeconds(){
        return jwtProperty.getExp_refresh()/1000;
    }
}
