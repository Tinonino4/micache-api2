package com.micache.mi_cache.analytics.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_skills_metrics")
public class UserSkillsMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Double teamwork;

    @Column(name = "self_confidence")
    private Double selfConfidence;

    private Double proactivity;
    private Double integrity;
    private Double flexibility;

    @Column(name = "average_score")
    private Double averageScore;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Constructor por defecto ---
    public UserSkillsMetric() {}

    // --- Constructor Factory para valores iniciales (todo a 0) ---
    public static UserSkillsMetric createEmpty(Long userId) {
        UserSkillsMetric metrics = new UserSkillsMetric();
        metrics.userId = userId;
        metrics.teamwork = 0.0;
        metrics.selfConfidence = 0.0;
        metrics.proactivity = 0.0;
        metrics.integrity = 0.0;
        metrics.flexibility = 0.0;
        metrics.averageScore = 0.0;
        metrics.updatedAt = LocalDateTime.now();
        return metrics;
    }

    // --- Getters y Setters Manuales ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTeamwork() { return teamwork; }
    public void setTeamwork(Double teamwork) { this.teamwork = teamwork; }

    public Double getSelfConfidence() { return selfConfidence; }
    public void setSelfConfidence(Double selfConfidence) { this.selfConfidence = selfConfidence; }

    public Double getProactivity() { return proactivity; }
    public void setProactivity(Double proactivity) { this.proactivity = proactivity; }

    public Double getIntegrity() { return integrity; }
    public void setIntegrity(Double integrity) { this.integrity = integrity; }

    public Double getFlexibility() { return flexibility; }
    public void setFlexibility(Double flexibility) { this.flexibility = flexibility; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
