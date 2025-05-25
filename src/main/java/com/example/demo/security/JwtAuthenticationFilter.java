package com.example.demo.security;

import com.example.demo.service.BlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String servletPath = request.getServletPath(); // извлечение пути запроса
        System.out.println("Servlet path: " + servletPath);

        if (servletPath.equals("/") ||
                servletPath.startsWith("/auth/login") || servletPath.equals("/api/auth/login") ||
            servletPath.equals("/auth/register/client") ||
            servletPath.equals("/auth/register/admin")) {
            System.out.println("Skipping JWT filter for: " + servletPath);
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);  // значение токена
        System.out.println("Token found: " + token + "Логин владельца токена: " );

        if (token == null) {
            System.out.println("Token is missing in the request");
        } else {
            System.out.println("Token found: " + token);
        }

        // Проверяем, находится ли токен в черном списке
        if (blacklistService.isTokenBlacklisted(token)) {
            System.out.println("Token is blacklisted");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is blacklisted");
            return; // Завершаем обработку запроса
        }

        if (token != null && jwtTokenService.validateToken(token)) {
            String username = jwtTokenService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("Roles: " + authentication.getAuthorities());


            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else {
            // Добавьте логирование для ошибок валидации токена
            System.out.println("Invalid or expired token");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Добавленная проверка куки:
        System.out.println("Блядский куки 1");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("Блядский куки 2");
            for (Cookie cookie : cookies) {
                System.out.println("Блядский куки 3");
                if ("jwt".equals(cookie.getName())) {
                    System.out.println("Блядский куки 4");
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
