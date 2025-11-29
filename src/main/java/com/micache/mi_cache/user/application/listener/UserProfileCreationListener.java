package com.micache.mi_cache.user.application.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.user.domain.User;
import com.micache.mi_cache.user.domain.UserProfile;
import com.micache.mi_cache.user.domain.repository.UserAccountRepository;
import com.micache.mi_cache.user.domain.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserProfileCreationListener {

    private static final String DEFAULT_PHOTO = "https://randomuser.me/api/portraits/lego/1.jpg";

    private final UserProfileRepository userProfileRepository;
    private final UserAccountRepository userRepository;

    @EventListener(UserRegistrationCompletedEvent.class)
    public void onUserRegistrationCompleted(UserRegistrationCompletedEvent event) {
        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(event.name());
        profile.setPhoto(DEFAULT_PHOTO);

        userProfileRepository.save(profile);
    }
}
