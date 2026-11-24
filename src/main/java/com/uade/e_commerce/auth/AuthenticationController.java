package com.uade.e_commerce.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.auth.dto.AuthResponseDTO;
import com.uade.e_commerce.auth.dto.LoginRequestDTO;
import com.uade.e_commerce.auth.dto.RegisterRequestDTO;
import com.uade.e_commerce.security.JwtCookieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtCookieService jwtCookieService;

    public AuthenticationController(AuthenticationService authenticationService, JwtCookieService jwtCookieService) {
        this.authenticationService = authenticationService;
        this.jwtCookieService = jwtCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        AuthResponseDTO auth = authenticationService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, jwtCookieService.crear(auth.getToken()).toString())
                .body(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO auth = authenticationService.login(requestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookieService.crear(auth.getToken()).toString())
                .body(auth);
    }

    // El front no puede borrar una cookie httpOnly: se la pisa el back con una vencida
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, jwtCookieService.borrar().toString())
                .build();
    }

    // Datos del usuario de la cookie: el front lo usa al cargar la página para saber si la sesión sigue activa
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authenticationService.me(user));
    }
}
