package com.uade.e_commerce.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Autentica cada request con el JWT de la cookie httpOnly. Sin cookie (o con una inválida) el request sigue como
 * anónimo: los endpoints públicos andan igual y los protegidos responden 401 desde SecurityConfig.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtCookieService jwtCookieService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
            JwtCookieService jwtCookieService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtCookieService = jwtCookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtCookieService.readToken(request);

        if (token != null && !authenticate(token, request)) {
            // Token vencido o inválido: borramos la cookie para que el navegador no la siga mandando
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookieService.clearCookie().toString());
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticate(String token, HttpServletRequest request) {
        try {
            String username = jwtService.extractUsername(token);
            if (username == null || username.isBlank()) {
                return false;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtService.isTokenValid(token, userDetails)) {
                return false;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
            return false;
        }
    }
}
