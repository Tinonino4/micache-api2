package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.ExperienceResponse;
import com.micache.mi_cache.model.Experience;
import com.micache.mi_cache.repository.ExperienceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceService experienceService;

    @Test
    void shouldReturnExperiences() {
        Experience exp = new Experience();
        exp.setCompanyName("EUROPASTRY");

        when(experienceRepository.findByUserIdOrderByIdDesc(1L))
                .thenReturn(List.of(exp));

        List<ExperienceResponse> experiences = experienceService.getExperiencesByUser(1L);

        assertEquals(1, experiences.size());
        assertEquals("EUROPASTRY", experiences.get(0).companyName());
    }
}