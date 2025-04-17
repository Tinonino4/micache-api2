package com.micache.mi_cache.controller;

import com.micache.mi_cache.dto.ExperienceResponse;
import com.micache.mi_cache.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping("/{id}/experiences")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByUser(@PathVariable Long id) {
        return ResponseEntity.ok(experienceService.getExperiencesByUser(id));
    }
}
