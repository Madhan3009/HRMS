package com.example.HRMS.config;

import com.example.HRMS.DTO.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {

    public String extractEmail(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    public Claims extractAllClaims(String jwt){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public SecretKey getSignKey(){
        byte[] keyBytes = "cL1LG<T1g;[s'Ah,+4yC1,I_PAV'``%k:E-d1{JufpHmD_&rz!0xgq{~M_(Zp".getBytes();
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaim(String jwt, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String generateToken(
            LoginRequest loginRequest){
        return Jwts.builder()
                .subject(loginRequest.getEmail())
                .signWith(getSignKey())
                .issuedAt(Date.from(java.time.Instant.now()))
                .expiration(Date.from(java.time.Instant.now().plusSeconds(1800) ))
                .compact();
    }

    public boolean isValidateToken(String jwt,UserDetails userDetails){
        final String email = extractEmail(jwt);
        return (email.equals(userDetails.getUsername()) && isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return extractClaim(jwt,Claims::getExpiration).before(new Date());
    }

    
}
