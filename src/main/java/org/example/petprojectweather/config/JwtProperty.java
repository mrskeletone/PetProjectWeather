package org.example.petprojectweather.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperty {
    @Value("${jwt.secret_code}")
    private  String secretCode ;
    @Value("${jwt.exp_refresh}")
    private long exp_refresh;
    @Value("${jwt.exp_access}")
    private long exp_access;

    public String getSecretCode() {
        return secretCode;
    }

    public long getExp_refresh() {
        return exp_refresh;
    }

    public long getExp_access() {
        return exp_access;
    }
}
