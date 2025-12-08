package com.micache.mi_cache.career.application;

import com.micache.mi_cache.career.application.dto.CreateExperienceRequest;
import com.micache.mi_cache.career.application.dto.ExperienceResponse;
import com.micache.mi_cache.career.domain.Experience;
import com.micache.mi_cache.career.domain.ExperienceRepository;
import com.micache.mi_cache.career.domain.exception.InvalidDatesExperienceException;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceService {

    private static final Logger log = LoggerFactory.getLogger(ExperienceService.class);

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceService(ExperienceRepository experienceRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.experienceRepository = experienceRepository;
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getMyExperiences(String email) {
        log.info("Recuperando experiencias para el usuario: {}", email);
        // Validamos que el usuario exista (opcional aquí, pero buena práctica)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Obtenemos solo las experiencias de ESTE email
        List<Experience> experiences = experienceRepository.findAllByUserId(user.getId());

        return experiences.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExperienceResponse createExperience(String email, CreateExperienceRequest request) {
        checkExperienceDates(request.startDate(), request.finishDate());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para email: " + email));

        Experience experience = createExperience(request, user.getId());
        Experience savedExperience = experienceRepository.save(experience);
        log.info("Experiencia creada exitosamente con ID: {}", savedExperience.getId());

        return mapToResponse(savedExperience);
    }

    private static Experience createExperience(CreateExperienceRequest request, Long userId) {
        Experience experience = new Experience();
        experience.setUserId(userId);
        experience.setCompanyName(request.companyName());
        experience.setDepartment(request.department());
        experience.setPosition(request.position());
        experience.setStartDate(request.startDate());
        experience.setFinishDate(request.finishDate());
        experience.setFunctions(request.functions());
        LocalDateTime now = LocalDateTime.now();
        experience.setCreatedAt(now);
        experience.setUpdatedAt(now);
        return experience;
    }

    private static void checkExperienceDates(LocalDate startDate, LocalDate finishDate) {
        if (finishDate != null && finishDate.isBefore(startDate)) {
            throw new InvalidDatesExperienceException("La fecha de finalización no puede ser anterior a la fecha de inicio.");
        }
    }

    @Transactional
    public void deleteExperience(String email, Long experienceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Experience experience = experienceRepository.findByIdAndUserId(experienceId, user.getId())
                .orElseThrow(() -> {
                    log.warn("Intento de borrado fallido. ID: {} no existe o no pertenece a {}", experienceId, user.getId());
                    return new ResourceNotFoundException("Experiencia no encontrada");
                });

        experienceRepository.delete(experience);
        log.info("Experiencia {} eliminada correctamente por {}", experienceId, email);
    }

    private ExperienceResponse mapToResponse(Experience e) {
        return new ExperienceResponse(
                e.getId(),
                e.getCompanyName(),
                e.getDepartment(),
                e.getPosition(),
                e.getStartDate(),
                e.getFinishDate(),
                e.getFunctions()
        );
    }
}
