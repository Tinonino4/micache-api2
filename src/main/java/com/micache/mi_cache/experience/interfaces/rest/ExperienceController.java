package com.micache.mi_cache.experience.interfaces.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micache.mi_cache.experience.application.ExperienceQueryService;
import com.micache.mi_cache.experience.interfaces.rest.dto.ExperienceResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceQueryService experienceService;

    @GetMapping("/{id}/experiences")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByUser(@PathVariable Long id) {
        return ResponseEntity.ok(experienceService.getExperiencesByUser(id));
    }
}
