package com.uade.e_commerce.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uade.e_commerce.common.dto.ErrorResponseDTO;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtCookieService jwtCookieService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
            ObjectMapper objectMapper, JwtCookieService jwtCookieService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.jwtCookieService = jwtCookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // El front manda el token en la cookie httpOnly; Postman u otros clientes pueden seguir usando el header
        String authorizationHeader = request.getHeader("Authorization");
        boolean desdeHeader = authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
        String token = desdeHeader ? authorizationHeader.substring(7).trim() : jwtCookieService.leer(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (token.isEmpty()) {
            writeUnauthorized(request, response, "Bearer token is missing");
            return;
        }

        if (!autenticar(token, request)) {
            if (desdeHeader) {
                writeUnauthorized(request, response, "Invalid or expired token");
                return;
            }
            // Cookie con un token inválido (por ejemplo, firmado con otro secreto): la borramos y seguimos
            // como anónimo, así los endpoints públicos (login, catálogo) siguen andando. Los protegidos
            // responden 401 desde el authenticationEntryPoint de SecurityConfig.
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookieService.borrar().toString());
        }

        filterChain.doFilter(request, response);
    }

    private boolean autenticar(String token, HttpServletRequest request) {
        try {
            String username = jwtService.extractUsername(token);
            if (username == null || username.isBlank()) {
                return false;
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                return true;
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

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponseDTO body = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), message, request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
