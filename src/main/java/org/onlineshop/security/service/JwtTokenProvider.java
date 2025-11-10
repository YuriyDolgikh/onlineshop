package org.onlineshop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.onlineshop.security.exception.InvalidJwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenProvider {

    private final String jwtSecret = "ksdjbvlerbvleiaurhfliuefgewriu37845ty7cno8734tycn8yfnsirefhkhireufh"; // Must be a minimum 32 bytes
    private final long jwtLifeTime = 3600000; // 60 minutes

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
     * @IllegalArgumentException - JWT claims is empty
     */
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
    public String getUsernameFromJwt(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claimsPayload = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claimsPayload.getSubject();
    }
}
