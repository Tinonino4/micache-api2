package com.micache.mi_cache.user.application;

import com.micache.mi_cache.auth.domain.events.UserRegistrationCompletedEvent;
import com.micache.mi_cache.model.UserProfile;
import com.micache.mi_cache.repository.UserProfileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserProfileCreationListener {

    private static final String DEFAULT_PHOTO = "https://randomuser.me/api/portraits/lego/1.jpg";

    private final UserProfileRepository userProfileRepository;

    public UserProfileCreationListener(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @EventListener
    public void handle(UserRegistrationCompletedEvent event) {
        UserProfile profile = new UserProfile();
        profile.setUserId(event.userId());
        profile.setName(event.name());
        profile.setContactEmail(event.email());
        profile.setPhoto(DEFAULT_PHOTO);

        userProfileRepository.save(profile);
    }
}
