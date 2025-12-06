package com.micache.mi_cache.dto;

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
