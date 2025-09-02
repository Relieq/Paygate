package com.example.paygate.controller;

import com.example.paygate.dto.auth.AuthReponse;
import com.example.paygate.dto.auth.RegisterRequest;
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
    public AuthReponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }
}
