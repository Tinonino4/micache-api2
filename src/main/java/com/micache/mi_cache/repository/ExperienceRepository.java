package com.micache.mi_cache.repository;

import com.micache.mi_cache.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUserIdOrderByIdDesc(Long userId);
}
