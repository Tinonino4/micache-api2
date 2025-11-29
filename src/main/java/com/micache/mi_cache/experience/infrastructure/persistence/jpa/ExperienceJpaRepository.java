package com.micache.mi_cache.experience.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micache.mi_cache.experience.domain.Experience;
import com.micache.mi_cache.experience.domain.repository.ExperienceRepository;

@Repository
public interface ExperienceJpaRepository extends JpaRepository<Experience, Long>, ExperienceRepository {
    @Override
    List<Experience> findByUserIdOrderByIdDesc(Long userId);
}
