package com.micache.mi_cache.dashboard.application;

import com.micache.mi_cache.analytics.domain.UserSkillsMetric;
import com.micache.mi_cache.analytics.infrastructure.UserSkillsMetricRepository;
import com.micache.mi_cache.career.application.dto.ExperienceResponse;
import com.micache.mi_cache.career.domain.Experience;
import com.micache.mi_cache.career.domain.ExperienceRepository;
import com.micache.mi_cache.dashboard.dto.DashboardResponse;
import com.micache.mi_cache.dto.UserProfileResponse;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSkillsMetricRepository metricsRepository;
    private final ExperienceRepository experienceRepository;

    public DashboardService(UserRepository userRepository,
                            UserProfileRepository userProfileRepository,
                            UserSkillsMetricRepository metricsRepository,
                            ExperienceRepository experienceRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.metricsRepository = metricsRepository;
        this.experienceRepository = experienceRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getUserDashboard(String email) {
        // 1. Obtener Usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Long userId = user.getId();

        // 2. Obtener Perfil
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        UserProfileResponse profileDto = new UserProfileResponse(
                profile.getName(),
                profile.getContactEmail() != null ? profile.getContactEmail() : user.getEmail(),
                profile.getCity(),
                profile.getEducation(),
                profile.getJobTitle()
        );

        // 3. Obtener Métricas (Si no existen, devolvemos 0)
        UserSkillsMetric metrics = metricsRepository.findByUserId(userId)
                .orElse(UserSkillsMetric.createEmpty(userId));

        DashboardResponse.SkillsMetricsResponse metricsDto = new DashboardResponse.SkillsMetricsResponse(
                metrics.getAverageScore(),
                metrics.getTeamwork(),
                metrics.getSelfConfidence(),
                metrics.getProactivity(),
                metrics.getIntegrity(),
                metrics.getFlexibility()
        );

        // 4. Obtener Experiencias (Ordenadas por fecha descendente)
        List<Experience> experiences = experienceRepository.findAllByUserIdOrderByStartDateDesc(userId);

        List<ExperienceResponse> experiencesDto = experiences.stream()
                .map(e -> new ExperienceResponse(
                        e.getId(),
                        e.getCompanyName(),
                        e.getDepartment(),
                        e.getPosition(),
                        e.getStartDate(),
                        e.getFinishDate(),
                        e.getFunctions()
                ))
                .collect(Collectors.toList());

        // 5. Ensamblar todo
        return new DashboardResponse(profileDto, metricsDto, experiencesDto);
    }
}
