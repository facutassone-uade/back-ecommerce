package com.uade.e_commerce.security;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.auth.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMilliseconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms:3600000}") long expirationMilliseconds) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET must be configured");
        }
        if (expirationMilliseconds <= 0) {
            throw new IllegalArgumentException("JWT_EXPIRATION_MS must be greater than zero");
        }
        this.signingKey = createSigningKey(secret);
        this.expirationMilliseconds = expirationMilliseconds;
    }

    public String generateToken(User user) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMilliseconds);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        return Objects.equals(claims.getSubject(), userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey createSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
