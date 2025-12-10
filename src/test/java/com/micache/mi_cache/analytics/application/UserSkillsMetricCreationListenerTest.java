package com.micache.mi_cache.analytics.application;

import com.micache.mi_cache.analytics.domain.UserSkillsMetric;
import com.micache.mi_cache.analytics.infrastructure.UserSkillsMetricRepository;
import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserSkillsMetricCreationListenerTest {

    @Mock
    private UserSkillsMetricRepository repository;

    @InjectMocks
    private UserSkillsMetricCreationListener listener;

    @Test
    void handle_ShouldInitializeMetricsToZero() {
        // GIVEN
        Long userId = 123L;
        UserRegistrationCompletedEvent event = new UserRegistrationCompletedEvent(userId, "test@mail.com", "Test");

        // WHEN
        listener.handle(event);

        // THEN
        ArgumentCaptor<UserSkillsMetric> captor = ArgumentCaptor.forClass(UserSkillsMetric.class);
        verify(repository).save(captor.capture());

        UserSkillsMetric savedMetric = captor.getValue();
        assertEquals(userId, savedMetric.getUserId());
        assertEquals(0.0, savedMetric.getAverageScore());
        assertEquals(0.0, savedMetric.getTeamwork());
        // Confirmamos que se guarda un objeto limpio
    }
}