package com.micache.mi_cache.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.user.domain.UserProfile;
import com.micache.mi_cache.user.domain.repository.UserAccountRepository;
import com.micache.mi_cache.user.domain.repository.UserProfileRepository;
import com.micache.mi_cache.user.interfaces.rest.dto.UserProfileResponse;

@ExtendWith(MockitoExtension.class)
class UserProfileQueryServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private UserProfileQueryService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileQueryService(userRepository, userProfileRepository);
    }

    @Test
    void returnsProfileForAuthenticatedUser() {
        User user = User.builder().id(1L).email("user@example.com").build();
        UserProfile profile = UserProfile.builder()
                .user(user)
                .name("John Doe")
                .city("New York")
                .education("BS Computer Science")
                .jobTitle("Engineer")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = service.getMyProfile("user@example.com");

        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.city()).isEqualTo("New York");
    }

    @Test
    void throwsWhenProfileIsMissing() {
        User user = User.builder().id(2L).email("missing@example.com").build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMyProfile(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");
    }
}
