package com.example.paygate.dto.auth;

public record TokenResponse(String accessToken, long expiresAtEpochSec) {}
