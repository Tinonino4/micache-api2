package com.micache.mi_cache.career.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateExperienceRequest(
        @NotBlank String companyName,
        String department,
        @NotBlank String position,
        @NotNull LocalDate startDate,
        LocalDate finishDate,
        String functions
) {}
