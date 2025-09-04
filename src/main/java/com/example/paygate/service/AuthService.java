package com.example.paygate.service;

import com.example.paygate.dto.auth.AuthResponse;
import com.example.paygate.dto.auth.AuthRequest;
import com.example.paygate.dto.auth.TokenResponse;
import com.example.paygate.entity.User;
import com.example.paygate.repository.UserRepository;
import com.example.paygate.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder; // <-- bean từ SecurityBeans
    private final JwtTokenProvider jwt; // <-- bean từ SecurityBeans

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtTokenProvider jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(AuthRequest req) {
        // Check mail trùng lặp
        if (users.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // Tạo user mới
        User user = new User(
                req.email(),
                encoder.encode(req.password()), // Mã hóa mật khẩu trước khi lưu
                "MERCHANT" // Vai trò mặc định
        );

        users.save(user);

        return new AuthResponse(user.getId());
    }

    @Transactional(readOnly = true)
    public TokenResponse login(AuthRequest req) {
        User user = users.findByEmail(req.email()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        );

        // Kiểm tra mật khẩu
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        var roles = Arrays.asList(user.getRole().split(","));
        var token = issueToken(user.getId().toString(), user.getEmail(), roles);
        var exp = jwt.expiresAtFromNow().getEpochSecond();

        return new TokenResponse(token, exp);
    }

    private String issueToken(String userId, String email, java.util.List<String> roles) {
        try {
            return jwt.issue(userId, email, roles);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot issue token");
        }
    }
}
