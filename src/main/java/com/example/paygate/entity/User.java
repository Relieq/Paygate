package com.example.paygate.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name="users",
    indexes = {
            @Index(name = "ux_users_email", columnList = "email", unique = true)
    }
)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // MySQL có limit 191 cho index unique trên InnoDB với UTF8MB4 để tránh lỗi key too long
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // "MERCHANT" or "ADMIN, MERCHANT".

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // ===== Lifecycle callback (hooks, entity listeners) =====
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Constructors
    public User() {}
    public User(String email, String passwordHash, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
