package com.micache.mi_cache.auth.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userId; // Desacoplado: Solo guardamos el ID

    @Column(nullable = false)
    private Instant expiryDate;

    // --- Constructor ---
    public RefreshToken() {}

    // --- Getters y Setters Manuales ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }

    // --- Lógica de Negocio ---
    // Método helper para crear uno nuevo fácilmente
    public static RefreshToken create(Long userId, long durationMs) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setToken(UUID.randomUUID().toString()); // Token opaco seguro
        rt.setExpiryDate(Instant.now().plusMillis(durationMs));
        return rt;
    }
}
