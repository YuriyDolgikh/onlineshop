package org.onlineshop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Generated;
import org.onlineshop.security.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.life.time}")
    private long jwtLifeTime; // 60 minutes

    @PostConstruct
    public void init() {
        validateConfiguration();
    }

    /**
     * Validates the configuration properties required for JWT token generation and usage.
     * Ensures the presence, format, and security criteria of the 'jwt.secret' and 'jwt.life.time' properties.
     * Throws an IllegalStateException if any of the following conditions are not met:
     *
     * - The 'jwt.secret' property must not be null, empty, or consist solely of whitespace.
     * - The 'jwt.secret' property must have a minimum length of 32 characters for compatibility with the HS256 algorithm.
     * - The 'jwt.life.time' property must be a positive value, indicating the token's validity duration.
     * - The 'jwt.life.time' property must not exceed a maximum of one day (24 hours).
     * - The 'jwt.life.time' property must be no less than 10 minutes.
     */
    private void validateConfiguration() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured. Please set 'jwt.secret' property.");
        }

        if (jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT secret is too short. Must be at least 32 characters for HS256 algorithm.");
        }

        if (jwtLifeTime <= 0) {
            throw new IllegalStateException("JWT lifetime must be positive. Please set 'jwt.life.time' property > 0.");
        }

        if (jwtLifeTime > 24 * 60 * 60 * 1000L) {
            throw new IllegalStateException("JWT lifetime is too long. Maximum allowed is 1 day.");
        }

        if (jwtLifeTime < 10 * 60 * 1000L) {
            throw new IllegalStateException("JWT lifetime is too short. Minimum allowed is 10 minutes.");
        }
    }

    /**
     * Creates a JWT token for the specified username.
     *
     * @param username the username for which the token is created
     * @return a String representation of the generated JWT token
     */
    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtLifeTime);
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validation of JWT token, received from User
     *
     * @param token JWT token
     * @return boolean variable with a result of validation. True, if the token is valid, otherwise will be thrown an exception:
     * @SignatureException - Invalid JWT Signature
     * @MalformedJwtException - Invalid JWT token
     * @ExpiredJwtException - Expired Jwt token
     * @UnsupportedJwtException - Unsupported Jwt token
     * @IllegalArgumentException - JWT claims are empty
     */
    @Generated
    public boolean validateToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (JwtException e) {
            throw new InvalidJwtException("Invalid JWT token: " + e.getMessage());
        }
        return true;
    }

    /**
     * Get username from JWT token, received from User
     * @param token JWT token - String variable
     * @return - String variable with username (email of user)
     */
    @Generated
    public String getUsernameFromJwt(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claimsPayload = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsPayload.getSubject();
    }
}
