package com.micache.mi_cache.career.application.dto;

import java.time.LocalDate;

public record ExperienceResponse(
        Long id,
        String companyName,
        String department,
        String position,
        LocalDate startDate,
        LocalDate finishDate,
        String functions
) {
}
