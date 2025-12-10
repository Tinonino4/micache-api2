package com.micache.mi_cache.service;

import com.micache.mi_cache.career.application.ExperienceService;
import com.micache.mi_cache.career.application.dto.CreateExperienceRequest;
import com.micache.mi_cache.career.application.dto.ExperienceResponse;
import com.micache.mi_cache.career.domain.Experience;
import com.micache.mi_cache.career.domain.ExperienceRepository;
import com.micache.mi_cache.career.domain.exception.InvalidDatesExperienceException;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExperienceService experienceService;

    // Constantes para los tests
    private final String USER_EMAIL = "juan@empresa.com";
    private final Long USER_ID = 1L;
    private final Long EXP_ID = 100L;

    @Test
    @DisplayName("createExperience: Debería crear experiencia correctamente cuando el usuario existe")
    void createExperience_Success() {
        // GIVEN
        CreateExperienceRequest request = new CreateExperienceRequest(
                "Tech Corp",
                "Development",
                "Developer",
                LocalDate.now().minusYears(1),
                null, // finishDate null (trabajo actual),
                "Developing software"
        );
        // finishDate null (trabajo actual)

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(USER_EMAIL);

        Experience savedExperience = new Experience();
        savedExperience.setId(EXP_ID);
        savedExperience.setCompanyName("Tech Corp");
        savedExperience.setPosition("Developer");
        savedExperience.setStartDate(LocalDate.now().minusYears(1));
        savedExperience.setUserId(mockUser.getId());

        // Simulamos comportamiento de los repositorios
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUser));
        when(experienceRepository.save(any(Experience.class))).thenReturn(savedExperience);

        // WHEN
        ExperienceResponse response = experienceService.createExperience(USER_EMAIL, request);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(EXP_ID);
        assertThat(response.companyName()).isEqualTo("Tech Corp");

        // Verificamos que se llamó al save 1 vez
        verify(experienceRepository, times(1)).save(any(Experience.class));
    }

    @Test
    @DisplayName("createExperience: Debería lanzar excepción si el usuario no existe")
    void createExperience_UserNotFound() {
        // GIVEN
        CreateExperienceRequest request = new CreateExperienceRequest(
                "Tech Corp",
                "Development",
                "Developer",
                LocalDate.now().minusYears(1),
                null,
                "Developing software"
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> experienceService.createExperience(USER_EMAIL, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(experienceRepository, never()).save(any());
    }

    @Test
    @DisplayName("createExperience: Debería lanzar excepción si fecha fin es anterior a fecha inicio")
    void createExperience_InvalidDates() {
        // GIVEN
        CreateExperienceRequest request = new CreateExperienceRequest(
                "Tech Corp",
                "Development",
                "Developer",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2022, 1, 1),
                "Developing software"
        );

        // WHEN / THEN
        // Nota: Ni siquiera necesitamos mockear el repositorio porque la validación ocurre antes
        assertThatThrownBy(() -> experienceService.createExperience(USER_EMAIL, request))
                .isInstanceOf(InvalidDatesExperienceException.class)
                .hasMessageContaining("La fecha de finalización no puede ser anterior a la fecha de inicio.");

        verify(experienceRepository, never()).save(any());
    }

    @Test
    @DisplayName("getMyExperiences: Debería retornar lista de experiencias del usuario")
    void getMyExperiences_Success() {
        // GIVEN
        Experience exp = new Experience();
        exp.setId(1L);
        exp.setCompanyName("Old Job");

        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUser));
        when(experienceRepository.findAllByUserIdOrderByStartDateDesc(USER_ID)).thenReturn(List.of(exp));

        // WHEN
        List<ExperienceResponse> responses = experienceService.getMyExperiences(USER_EMAIL);

        // THEN
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).companyName()).isEqualTo("Old Job");
    }

    @Test
    @DisplayName("deleteExperience: Debería borrar si la experiencia existe y pertenece al usuario")
    void deleteExperience_Success() {
        // GIVEN
        Experience mockExp = new Experience();
        mockExp.setId(EXP_ID);

        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUser));
        // Mockeamos findByIdAndUserEmail para simular que ES el dueño
        when(experienceRepository.findByIdAndUserId(EXP_ID, USER_ID))
                .thenReturn(Optional.of(mockExp));

        // WHEN
        experienceService.deleteExperience(USER_EMAIL, EXP_ID);

        // THEN
        verify(experienceRepository, times(1)).delete(mockExp);
    }

    @Test
    @DisplayName("deleteExperience: Debería lanzar error si la experiencia no existe o no es del usuario")
    void deleteExperience_NotFoundOrNotOwner() {
        // GIVEN
        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUser));
        // Simulamos que no se encuentra la combinación ID + Email
        when(experienceRepository.findByIdAndUserId(EXP_ID, USER_ID))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> experienceService.deleteExperience(USER_EMAIL, EXP_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        // Aseguramos que NUNCA se llame al método delete
        verify(experienceRepository, never()).delete(any());
    }
}