package com.micache.mi_cache.user.application;

import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import com.micache.mi_cache.security.repository.UserRepository;
import com.micache.mi_cache.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserProfileCreationListener {

    private static final String DEFAULT_PHOTO = "https://randomuser.me/api/portraits/lego/1.jpg";

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileCreationListener(UserProfileRepository userProfileRepository,
                                       UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handle(UserRegistrationCompletedEvent event) {
        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(event.name());
        profile.setPhoto(DEFAULT_PHOTO);

        userProfileRepository.save(profile);
    }
}
