package com.example.demo.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AppRole role;

    protected UserAccount() {
    }

    public UserAccount(String username, String passwordHash, AppRole role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AppRole getRole() {
        return role;
    }

    public void setAdminCredentials(String passwordHash) {
        this.role = AppRole.ADMIN;
        this.passwordHash = passwordHash;
    }
}
