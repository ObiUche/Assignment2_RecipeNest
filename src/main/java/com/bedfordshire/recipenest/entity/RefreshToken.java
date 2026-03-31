package com.bedfordshire.recipenest.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // refresh token sent to client
    @Column(nullable = false, unique = true, length = 200)
    private String token;

    // When this refresh token expires
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Allows the token to be invalidated before expiry
    @Column(nullable = false)
    private boolean revoked = false;

    // The user the token belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken(){
    }

    public RefreshToken(User user){
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusDays(30);
        this.revoked = false;
    }

    // Marks the token as unusable
    public void revoke(){
        this.revoked = true;
    }

    // Checks if token should no longer be accepted
    public boolean isExpired(){
        return revoked || LocalDateTime.now().isAfter(expiryDate);
    }

    // Reuse the same DB row but generates a fresh token and expiry date
    public void regenerate(){
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusDays(30);
        this.revoked = false;
    }

    // Ensures the token and expiry exist before saving for the first time
    @PrePersist
    protected void onCreated(){
        if(this.token == null){
            this.token = UUID.randomUUID().toString();
        }

        if(this.expiryDate == null){
            this.expiryDate = LocalDateTime.now().plusDays(30);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

}
