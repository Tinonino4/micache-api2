package com.micache.mi_cache.career.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {
    // Métodos de lectura
    List<Experience> findAllByUserId(Long userId);
    Optional<Experience> findByIdAndUserId(Long id, Long userId);

    // Métodos de escritura (Spring Data implementará save y delete automáticamente si las firmas coinciden)
    Experience save(Experience experience);
    void delete(Experience experience);
}