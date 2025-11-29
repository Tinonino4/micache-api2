package com.micache.mi_cache.experience.interfaces.rest.dto;

import java.time.LocalDate;

import com.micache.mi_cache.experience.domain.Experience;

public record ExperienceResponse(
        Long id,
        String companyName,
        String department,
        String position,
        LocalDate startDate,
        LocalDate finishDate,
        String functions
) {
    public static ExperienceResponse fromDomain(Experience experience) {
        return new ExperienceResponse(
                experience.getId(),
                experience.getCompanyName(),
                experience.getDepartment(),
                experience.getPosition(),
                experience.getStartDate(),
                experience.getFinishDate(),
                experience.getFunctions()
        );
    }
}
