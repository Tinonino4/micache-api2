package com.micache.mi_cache.dashboard.application;

import com.micache.mi_cache.analytics.domain.UserSkillsMetric;
import com.micache.mi_cache.analytics.infrastructure.UserSkillsMetricRepository;
import com.micache.mi_cache.career.domain.Experience;
import com.micache.mi_cache.career.domain.ExperienceRepository;
import com.micache.mi_cache.dashboard.dto.DashboardResponse;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserSkillsMetricRepository metricsRepository;
    @Mock private ExperienceRepository experienceRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                userRepository,
                userProfileRepository,
                metricsRepository,
                experienceRepository
        );
    }

    @Test
    @DisplayName("Debe devolver el dashboard completo cuando todo existe")
    void getUserDashboard_Success() {
        // GIVEN
        String email = "juan@test.com";
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setName("Juan");
        profile.setCity("Madrid");

        UserSkillsMetric metrics = new UserSkillsMetric();
        metrics.setAverageScore(4.5);
        metrics.setTeamwork(5.0);

        Experience exp = new Experience();
        exp.setId(10L);
        exp.setCompanyName("Tech SL");
        exp.setStartDate(LocalDate.now());

        // Mocks
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(metricsRepository.findByUserId(userId)).thenReturn(Optional.of(metrics));
        when(experienceRepository.findAllByUserIdOrderByStartDateDesc(userId)).thenReturn(List.of(exp));

        // WHEN
        DashboardResponse response = dashboardService.getUserDashboard(email);

        // THEN
        assertNotNull(response);
        assertEquals("Juan", response.profile().name());
        assertEquals(4.5, response.metrics().globalAverage());
        assertEquals(1, response.experiences().size());
        assertEquals("Tech SL", response.experiences().get(0).companyName());
    }

    @Test
    @DisplayName("Debe devolver métricas a 0 si no existen en BD (Resiliencia)")
    void getUserDashboard_EmptyMetrics() {
        // GIVEN
        String email = "nuevo@test.com";
        Long userId = 2L;
        User user = new User(); user.setId(userId); user.setEmail(email);
        UserProfile profile = new UserProfile(); profile.setUserId(userId);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        // Simulamos que NO hay métricas
        when(metricsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(experienceRepository.findAllByUserIdOrderByStartDateDesc(userId)).thenReturn(Collections.emptyList());

        // WHEN
        DashboardResponse response = dashboardService.getUserDashboard(email);

        // THEN
        assertNotNull(response);
        assertNotNull(response.metrics());
        assertEquals(0.0, response.metrics().globalAverage()); // Debe ser 0, no null
        assertEquals(0.0, response.metrics().teamwork());
    }
}