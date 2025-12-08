package com.micache.mi_cache.security.model;

import java.time.LocalDateTime;

import com.micache.mi_cache.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean confirmed;

    // -----------------------------------------------------------------
    // 1. CONSTRUCTORES
    // -----------------------------------------------------------------

    public ConfirmationToken() {
    }

    public ConfirmationToken(Long id, String token, User user, LocalDateTime createdAt, LocalDateTime expiresAt, boolean confirmed) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmed = confirmed;
    }

    // -----------------------------------------------------------------
    // 2. LÓGICA DE NEGOCIO (Preservada del original)
    // -----------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.confirmed = false; // Default value when the token is created
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    // -----------------------------------------------------------------
    // 3. GETTERS Y SETTERS
    // -----------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    // -----------------------------------------------------------------
    // 4. EQUALS, HASHCODE Y TOSTRING
    // -----------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationToken that = (ConfirmationToken) o;
        // Usamos el token como clave única de igualdad
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "ConfirmationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", confirmed=" + confirmed +
                // Omitimos 'user' completo para evitar StackOverflowError,
                // pero podríamos imprimir su ID si fuera necesario.
                '}';
    }

    // -----------------------------------------------------------------
    // 5. BUILDER PATTERN
    // -----------------------------------------------------------------

    public static ConfirmationTokenBuilder builder() {
        return new ConfirmationTokenBuilder();
    }

    public static class ConfirmationTokenBuilder {
        private Long id;
        private String token;
        private User user;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean confirmed;

        ConfirmationTokenBuilder() {
        }

        public ConfirmationTokenBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConfirmationTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ConfirmationTokenBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ConfirmationTokenBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ConfirmationTokenBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public ConfirmationTokenBuilder confirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public ConfirmationToken build() {
            return new ConfirmationToken(id, token, user, createdAt, expiresAt, confirmed);
        }

        public String toString() {
            return "ConfirmationToken.ConfirmationTokenBuilder(id=" + this.id + ", token=" + this.token + ", user=" + this.user + ", createdAt=" + this.createdAt + ", expiresAt=" + this.expiresAt + ", confirmed=" + this.confirmed + ")";
        }
    }
}
