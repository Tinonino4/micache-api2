package com.micache.mi_cache.dto;


public record UserProfileResponse (
        String name,
        String contactEmail,
        String city,
        String education,
        String jobTitle
){
}