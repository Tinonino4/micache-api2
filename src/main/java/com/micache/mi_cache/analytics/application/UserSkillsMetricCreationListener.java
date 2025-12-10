package com.micache.mi_cache.analytics.application;

import com.micache.mi_cache.analytics.domain.UserSkillsMetric;
import com.micache.mi_cache.analytics.infrastructure.UserSkillsMetricRepository;
import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserSkillsMetricCreationListener {

    private static final Logger log = LoggerFactory.getLogger(UserSkillsMetricCreationListener.class);
    private final UserSkillsMetricRepository repository;

    public UserSkillsMetricCreationListener(UserSkillsMetricRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void handle(UserRegistrationCompletedEvent event) {
        log.info("Inicializando métricas vacías para el usuario ID: {}", event.userId());

        // Usamos el método factoría que creamos antes para inicializar a 0.0
        UserSkillsMetric initialMetrics = UserSkillsMetric.createEmpty(event.userId());

        repository.save(initialMetrics);
    }
}
