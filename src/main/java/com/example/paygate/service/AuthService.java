package com.example.paygate.service;

import com.example.paygate.dto.auth.AuthReponse;
import com.example.paygate.dto.auth.RegisterRequest;
import com.example.paygate.entity.User;
import com.example.paygate.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder; // <-- bean từ SecurityBeans

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public AuthReponse register(RegisterRequest req) {
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

        return new AuthReponse(user.getId());
    }
}
