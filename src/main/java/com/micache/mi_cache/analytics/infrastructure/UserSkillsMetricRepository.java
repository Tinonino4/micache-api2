package com.micache.mi_cache.analytics.infrastructure;

import com.micache.mi_cache.analytics.domain.UserSkillsMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSkillsMetricRepository extends JpaRepository<UserSkillsMetric, Long> {
    Optional<UserSkillsMetric> findByUserId(Long userId);
}
