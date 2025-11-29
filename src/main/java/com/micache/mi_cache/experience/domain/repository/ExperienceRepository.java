package com.micache.mi_cache.experience.domain.repository;

import java.util.List;

import com.micache.mi_cache.experience.domain.Experience;

public interface ExperienceRepository {
    List<Experience> findByUserIdOrderByIdDesc(Long userId);
}
