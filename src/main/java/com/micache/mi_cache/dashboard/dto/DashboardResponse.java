package com.micache.mi_cache.dashboard.dto;

import com.micache.mi_cache.dto.UserProfileResponse; // Tu DTO existente de perfil
import com.micache.mi_cache.career.application.dto.ExperienceResponse; // Tu DTO existente de exp
import java.util.List;

public record DashboardResponse(
        UserProfileResponse profile,
        SkillsMetricsResponse metrics,
        List<ExperienceResponse> experiences
) {
    // Record interno para las métricas (estrellas)
    public record SkillsMetricsResponse(
            Double globalAverage, // Para mostrar la media general
            Double teamwork,
            Double selfConfidence,
            Double proactivity,
            Double integrity,
            Double flexibility
    ) {}
}
