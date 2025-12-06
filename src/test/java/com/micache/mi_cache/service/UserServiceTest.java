package com.micache.mi_cache.service;

import com.micache.mi_cache.dto.UserProfileResponse;
import com.micache.mi_cache.security.exception.ResourceNotFoundException;
import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserProfile() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");

        UserProfile profile = new UserProfile();
        profile.setName("Rocío");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = userService.getMyProfile("test@email.com");

        assertEquals("Rocío", response.name());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getMyProfile("wrong@email.com"));
    }
}