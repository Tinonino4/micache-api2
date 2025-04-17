package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.ExperienceResponse;
import com.micache.mi_cache.model.Experience;
import com.micache.mi_cache.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public List<ExperienceResponse> getExperiencesByUser(Long userId) {
        List<Experience> experiences = experienceRepository.findByUserIdOrderByIdDesc(userId);
        return experiences.stream()
                .map(e -> new ExperienceResponse(
                        e.getId(),
                        e.getCompanyName(),
                        e.getDepartment(),
                        e.getPosition(),
                        e.getStartDate(),
                        e.getFinishDate(),
                        e.getFunctions()
                ))
                .toList();
    }
}
