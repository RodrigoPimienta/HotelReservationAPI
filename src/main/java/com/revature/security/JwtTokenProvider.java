package com.revature.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // Generate JWT token
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = authentication.getName();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        Map<String, Object> claims = new HashMap<>();

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        claims.put("userId", customUserDetails.getUserId());
        claims.put("role", customUserDetails.getAuthorities().iterator().next().getAuthority());
        claims.put("firstName", customUserDetails.getFirstName());
        claims.put("lastName", customUserDetails.getLastName());

        return Jwts.builder()
                .setSubject(email)
                .claims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Get username from JWT token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("JWT claims string is empty");
        }
    }

    public <T> T getClaimFromToken(String token, String claimName, Class<T> type) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(claimName, type);
        } catch (Exception e) {
            return null;
        }
    }
}