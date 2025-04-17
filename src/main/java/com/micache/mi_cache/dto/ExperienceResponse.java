package com.micache.mi_cache.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ExperienceResponse {

    private Long id;
    private String companyName;
    private String department;
    private String position;
    private LocalDate startDate;
    private LocalDate finishDate;
    private String functions;
}
