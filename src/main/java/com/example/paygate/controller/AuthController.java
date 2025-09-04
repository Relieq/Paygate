package com.example.paygate.controller;

import com.example.paygate.dto.auth.AuthResponse;
import com.example.paygate.dto.auth.AuthRequest;
import com.example.paygate.dto.auth.TokenResponse;
import com.example.paygate.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody AuthRequest req) {
        return authService.login(req);
    }
}
