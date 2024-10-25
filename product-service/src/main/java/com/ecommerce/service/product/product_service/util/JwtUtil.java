package com.ecommerce.service.product.product_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${api.jwt.secret-key}")
    private String SECRET_KEY;

    // Retrieve the signing key from the secret key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Extract all claims from the token using parserBuilder()
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Use parserBuilder()
                .setSigningKey(getSigningKey()) // Set the signing key
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get claims
    }

    // Check if the token is expired
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate a new token for the username, including roles
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Include roles in the claims
        return createToken(claims, username);
    }

    // Create a token with specified claims and subject
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 50)) // 50 minutes expiration
                .signWith(getSigningKey())
                .compact();
    }

    // Validate the token and check expiration
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Validate the token by extracting claims
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false; // If an exception is thrown, the token is invalid
        }
    }
}
