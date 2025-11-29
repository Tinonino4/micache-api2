package com.micache.mi_cache.experience.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.micache.mi_cache.experience.domain.Experience;
import com.micache.mi_cache.experience.domain.repository.ExperienceRepository;
import com.micache.mi_cache.experience.interfaces.rest.dto.ExperienceResponse;

@ExtendWith(MockitoExtension.class)
class ExperienceQueryServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    private ExperienceQueryService service;

    @BeforeEach
    void setUp() {
        service = new ExperienceQueryService(experienceRepository);
    }

    @Test
    void returnsExperiencesForUser() {
        Experience experience = Experience.builder()
                .id(1L)
                .companyName("ACME")
                .department("R&D")
                .position("Engineer")
                .startDate(LocalDate.of(2020, 1, 1))
                .finishDate(LocalDate.of(2021, 12, 31))
                .functions("Build stuff")
                .build();

        when(experienceRepository.findByUserIdOrderByIdDesc(42L)).thenReturn(List.of(experience));

        List<ExperienceResponse> responses = service.getExperiencesByUser(42L);

        assertThat(responses)
                .hasSize(1)
                .first()
                .satisfies(response -> {
                    assertThat(response.companyName()).isEqualTo("ACME");
                    assertThat(response.position()).isEqualTo("Engineer");
                    assertThat(response.startDate()).isEqualTo(LocalDate.of(2020, 1, 1));
                });
    }
}
