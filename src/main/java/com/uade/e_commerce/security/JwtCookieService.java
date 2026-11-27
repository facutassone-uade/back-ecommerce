package com.uade.e_commerce.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Cookie httpOnly donde viaja el JWT. JavaScript no la puede leer: el navegador la manda sola en cada request a /api.
 */
@Component
public class JwtCookieService {

    public static final String COOKIE_NAME = "token";

    private final Duration maxAge;
    private final boolean secure;

    public JwtCookieService(@Value("${security.jwt.expiration-ms:3600000}") long expirationMilliseconds,
            @Value("${security.jwt.cookie-secure:false}") boolean secure) {
        this.maxAge = Duration.ofMillis(expirationMilliseconds);
        this.secure = secure;
    }

    public ResponseCookie createCookie(String token) {
        return buildCookie(token, maxAge);
    }

    // Cookie vencida: el navegador borra la que tenía guardada
    public ResponseCookie clearCookie() {
        return buildCookie("", Duration.ZERO);
    }

    public String readToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        return cookie == null || cookie.getValue().isBlank() ? null : cookie.getValue();
    }

    private ResponseCookie buildCookie(String value, Duration cookieMaxAge) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure) // en producción (HTTPS) va en true: JWT_COOKIE_SECURE=true
                .sameSite("Strict") // no viaja en requests iniciados desde otros sitios (protección CSRF)
                .path("/api")
                .maxAge(cookieMaxAge)
                .build();
    }
}
