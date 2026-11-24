package com.uade.e_commerce.security;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Maneja la cookie httpOnly donde viaja el JWT. Al ser httpOnly, el JavaScript del front no la puede leer:
 * el navegador la manda sola en cada request a /api.
 */
@Component
public class JwtCookieService {

    public static final String NOMBRE = "token";
    private static final String PATH = "/api";

    private final Duration duracion;
    private final boolean secure;

    public JwtCookieService(@Value("${security.jwt.expiration-ms:3600000}") long expirationMilliseconds,
            @Value("${security.jwt.cookie-secure:false}") boolean secure) {
        this.duracion = Duration.ofMillis(expirationMilliseconds);
        this.secure = secure;
    }

    public ResponseCookie crear(String token) {
        return construir(token, duracion);
    }

    /** Cookie vencida: el navegador borra la que tenía guardada. */
    public ResponseCookie borrar() {
        return construir("", Duration.ZERO);
    }

    public String leer(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> NOMBRE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElse(null);
    }

    private ResponseCookie construir(String valor, Duration maxAge) {
        return ResponseCookie.from(NOMBRE, valor)
                .httpOnly(true)
                .secure(secure) // en producción (HTTPS) va en true: JWT_COOKIE_SECURE=true
                .sameSite("Strict") // no viaja en requests iniciados desde otros sitios (protección CSRF)
                .path(PATH)
                .maxAge(maxAge)
                .build();
    }
}
