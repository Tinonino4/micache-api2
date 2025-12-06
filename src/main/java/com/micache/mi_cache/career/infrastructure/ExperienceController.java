package com.micache.mi_cache.career.infrastructure;

import com.micache.mi_cache.career.application.ExperienceService;
import com.micache.mi_cache.career.application.dto.CreateExperienceRequest;
import com.micache.mi_cache.career.application.dto.ExperienceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@Tag(name = "Carrera Profesional", description = "Gestión de experiencias laborales del usuario actual")
@SecurityRequirement(name = "bearerAuth")
public class ExperienceController {

    private static final Logger logger = LoggerFactory.getLogger(ExperienceController.class);

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @Operation(summary = "Obtener mis experiencias", description = "Recupera el historial laboral del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado recuperado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getMyExperiences(Authentication authentication) {
        // Extraemos el identificador principal (email/username) del token
        String userEmail = authentication.getName();

        logger.info("Solicitud de recuperación de experiencias para el usuario: {}", userEmail);

        // Delegamos al servicio la lógica de buscar al usuario por email y sus experiencias
        List<ExperienceResponse> experiences = experienceService.getMyExperiences(userEmail);

        return ResponseEntity.ok(experiences);
    }

    @Operation(summary = "Añadir nueva experiencia", description = "Crea una entrada laboral asociada al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Experiencia creada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ExperienceResponse> createExperience(
            Authentication authentication,
            @Parameter(description = "Datos de la experiencia a crear", required = true)
            @Valid @RequestBody CreateExperienceRequest request) {

        String userEmail = authentication.getName();
        logger.info("Usuario {} intentando crear nueva experiencia: {}", userEmail, request.position());

        // Pasamos el email, el servicio se encarga de resolver el ID del usuario
        ExperienceResponse response = experienceService.createExperience(userEmail, request);

        logger.info("Experiencia creada exitosamente para usuario {}", userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar experiencia", description = "Elimina una experiencia propia por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Experiencia no encontrada o no pertenece al usuario", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(
            Authentication authentication,
            @Parameter(description = "ID de la experiencia", required = true) @PathVariable Long id) {

        String userEmail = authentication.getName();
        logger.warn("Solicitud de eliminación de experiencia ID: {} por usuario: {}", id, userEmail);

        // El servicio debe validar que la experiencia pertenezca a ese email antes de borrar
        experienceService.deleteExperience(userEmail, id);

        logger.info("Experiencia ID: {} eliminada", id);
        return ResponseEntity.noContent().build();
    }
}