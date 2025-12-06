package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.ExperienceResponse;
import com.micache.mi_cache.model.Experience;
import com.micache.mi_cache.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    public List<ExperienceResponse> getExperiencesByUser(Long userId) {
        List<Experience> experiences = experienceRepository.findByUserIdOrderByIdDesc(userId);
        return experiences.stream()
                .map(experience -> new ExperienceResponse(
                        experience.getId(),
                        experience.getCompanyName(),
                        experience.getDepartment(),
                        experience.getPosition(),
                        experience.getStartDate(),
                        experience.getFinishDate(),
                        experience.getFunctions()
                ))
                .toList();
    }
}
