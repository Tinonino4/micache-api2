package com.micache.mi_cache.user.interfaces.rest.dto;

import com.micache.mi_cache.user.domain.UserProfile;

public record UserProfileResponse(
        String name,
        String city,
        String education,
        String jobTitle
) {
    public static UserProfileResponse fromDomain(UserProfile profile) {
        return new UserProfileResponse(
                profile.getName(),
                profile.getCity(),
                profile.getEducation(),
                profile.getJobTitle()
        );
    }
}
