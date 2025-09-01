package com.example.bankcards.security;

import com.example.bankcards.exception.InvalidJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;

    @Value("${jwt.expiration}")
    private long tokenValidityInMilliSeconds;

    @Value("${jwt.refresh-expiration-ms}")
    private long tokenRefreshValidityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret}")String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String name) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidityInMilliSeconds);
        return Jwts.builder().setSubject(name).setIssuedAt(now).setExpiration(expiry).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String name) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenRefreshValidityInMilliseconds);
        return Jwts.builder().setSubject(name).setIssuedAt(now).setExpiration(expiry).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String getNameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException();
        }
    }
}
