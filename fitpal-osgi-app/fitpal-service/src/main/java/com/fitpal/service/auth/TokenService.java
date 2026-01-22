package com.fitpal.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component(service = TokenService.class, configurationPid = "com.fitpal.app")
public class TokenService {

    private Key key;
    private long expirationTime = 86400000; // 1 day in ms

    @Activate
    public void activate(Map<String, Object> properties) {
        String secret = (String) properties.get("jwt.secret");
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret is required and must be at least 32 characters"
            );
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println("[TokenService] JWT initialized");
    }

    /**
     * Generate JWT token with userId and email claim
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract userId from token (NEW - to match JwtUtil)
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validate token and return userId if valid, null otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Invalid token
        }
    }
}