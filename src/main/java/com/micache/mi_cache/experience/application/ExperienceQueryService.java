package com.micache.mi_cache.experience.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.micache.mi_cache.experience.domain.repository.ExperienceRepository;
import com.micache.mi_cache.experience.interfaces.rest.dto.ExperienceResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperienceQueryService {

    private final ExperienceRepository experienceRepository;

    public List<ExperienceResponse> getExperiencesByUser(Long userId) {
        return experienceRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(ExperienceResponse::fromDomain)
                .toList();
    }
}
