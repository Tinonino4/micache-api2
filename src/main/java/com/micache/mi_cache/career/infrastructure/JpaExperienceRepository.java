package com.micache.mi_cache.career.infrastructure;

import com.micache.mi_cache.career.domain.Experience;
import com.micache.mi_cache.career.domain.ExperienceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Al extender de JpaRepository Y de nuestra interfaz de dominio,
// Spring implementa automáticamente los métodos coincidentes.
@Repository
public interface JpaExperienceRepository extends JpaRepository<Experience, Long>, ExperienceRepository {
    // No hace falta escribir código, Spring Data lo hace por el nombre del método
}
