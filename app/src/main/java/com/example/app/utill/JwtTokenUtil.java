package com.example.app.utill;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Duration jwtLifeTime;

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles",roles);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifeTime.toMillis());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .expiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token){
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token){
        return getClaimsFromToken(token).get("roles", List.class);
    }

    public Duration getJwtLifeTime() {
        return jwtLifeTime;
    }

    public void setJwtLifeTime(Duration jwtLifeTime) {
        this.jwtLifeTime = jwtLifeTime;
    }
}
