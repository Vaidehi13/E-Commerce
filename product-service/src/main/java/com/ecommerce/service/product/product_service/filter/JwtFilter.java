package com.ecommerce.service.product.product_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Value("${api.jwt.secret-key}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);

        // Validate the token and set the authentication context if valid
        try {
            Claims claims = extractAllClaims(jwtToken);
            String username = claims.getSubject();
            List<SimpleGrantedAuthority> authorities = getAuthoritiesFromClaims(claims);

            // Set the authentication in the SecurityContext
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // Log the error and respond with 401 Unauthorized if needed
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token.");
            return;
        }

        chain.doFilter(request, response);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Use parserBuilder
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean validateToken(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration != null && !expiration.before(new Date());
    }

    private List<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        List<String> roles = claims.get("roles", List.class); // Extract roles from claims
        return roles == null ? Collections.emptyList()
                : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}

