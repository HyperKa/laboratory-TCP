package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey; // Секретный ключ берется из application.properties

    private final long expirationTime = 86400000; // Время жизни токена (например, 24 часа)

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey) // Убираем .getBytes()
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            // Парсим токен и проверяем подпись
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // Проверяем, истек ли срок действия токена
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Если возникла ошибка (например, неверная подпись или истекший токен), возвращаем false
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)// Убираем .getBytes() и .build()
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateTokenFromLogin(String login, String role) {
        // Создаем временного пользователя с нужной ролью
        User userDetails = new User(
                login,
                "", // Пароль не нужен для генерации токена
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        return generateToken(userDetails);
    }

    /*
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    */

    // это нужно для фиксации времени, когда токен удаляется из блэклиста автоматически
    public LocalDateTime extractExpiration(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.toInstant()
                             .atZone(java.time.ZoneId.systemDefault())
                             .toLocalDateTime();
    }
}
