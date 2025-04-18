package com.example.demo.security;

import com.example.demo.service.BlacklistedTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    private BlacklistedTokenService blacklistedTokenService; // Добавляем сервис для работы с черным списком

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String servletPath = request.getServletPath(); // самый надёжный способ
        System.out.println("Servlet path: " + servletPath);

        if (servletPath.startsWith("/auth/login") || servletPath.startsWith("/auth/register")) {
            System.out.println("Skipping JWT filter for: " + servletPath);
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);

        if (token == null) {
            System.out.println("Token is missing in the request");
        } else {
            System.out.println("Token found: " + token);
        }

        try {
            // Проверяем, находится ли токен в черном списке
            if (token != null && blacklistedTokenService.isTokenBlacklisted(token)) {
                System.out.println("Token is blacklisted");
                throw new SecurityException("Token has been revoked");
            }

            // Валидируем токен
            if (token != null && jwtTokenService.validateToken(token)) {
                String username = jwtTokenService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Invalid or expired token");
            }
        } catch (SecurityException e) {
            // Логируем ошибку и отправляем ответ клиенту
            System.out.println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}