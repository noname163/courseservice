package com.example.courseservice.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.Common;
import com.example.courseservice.exceptions.InValidAuthorizationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {
    @Autowired
    private EnvironmentVariable environmentVariables;

    private String doGenerateToken(Map<String, Object> claims, String subject, Integer expriesTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() + environmentVariables.getExpireTime() * expriesTime))
                .signWith(SignatureAlgorithm.HS512, environmentVariables.getJwtSecret()).compact();
    }

    // public String generateJwtToken(Integer expiresTime) {
    //     Map<String, Object> claims = new HashMap<>();
    //     claims.put("email", user.getEmail());
    //     claims.put("role", user.getRole());
    //     claims.put("avatar", user.getImageURL());
    //     claims.put("fullName", user.getFullName());
    //     return doGenerateToken(claims, user.getEmail(), expiresTime);
    // }


    public Jws<Claims> getJwsClaims(String token, String from) {
        String secretKey = "";
        if(Common.BEARER.equals(from)){
            secretKey = environmentVariables.getJwtSecret();
        }
        if(Common.SERVICE.equals(from)){
            secretKey = environmentVariables.getJwtSecretService();
        }
        Jws<Claims> tokenInfor = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Claims claims = tokenInfor.getBody();

        // Check if the token has expired
        Date expirationDate = claims.getExpiration();
        Date now = new Date();

        if (expirationDate != null && expirationDate.before(now)) {
            throw new InValidAuthorizationException("Token has expired");
        }

        return tokenInfor;
    }
    public Jws<Claims> getJwsClaimsForService(String token) {
        Jws<Claims> tokenInfor = Jwts.parser().setSigningKey(environmentVariables.getJwtSecret()).parseClaimsJws(token);
        Claims claims = tokenInfor.getBody();

        Date expirationDate = claims.getExpiration();
        Date now = new Date();

        if (expirationDate != null && expirationDate.before(now)) {
            throw new InValidAuthorizationException("Token has expired");
        }

        return tokenInfor;
    }

    public Claims verifyRefreshToken(String refreshToken) {
        Jws<Claims> refreshTokenClaims = Jwts.parser().setSigningKey(environmentVariables.getJwtSecret()).parseClaimsJws(refreshToken);
        if (refreshTokenClaims.getBody().getExpiration().before(new Date())) {
            throw new InValidAuthorizationException("Refresh token has expired");
        }

        return refreshTokenClaims.getBody();
    }

    public String getEmailFromClaims(Claims claims) {
        return claims.get("email").toString();
    }

    public String getPhoneFromClaims(Claims claims) {
        return claims.get("phone").toString();
    }
}
