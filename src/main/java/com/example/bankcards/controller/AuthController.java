package com.example.bankcards.controller;

import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.dto.dtoRequest.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.name(), loginRequest.password()));
        String token = jwtTokenProvider.generateAccessToken(authenticate.getName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authenticate.getName());
        return ResponseEntity.ok(Map.of("token", token, "refreshtoken", refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> refreshRequest) {
        String refreshToken = refreshRequest.get("refreshtoken");
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String nameFromToken = jwtTokenProvider.getNameFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(nameFromToken);
            return ResponseEntity.ok(Map.of("token", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Ошибка", "Некорректный refresh токен"));
        }
    }
}
