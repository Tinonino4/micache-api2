package com.micache.mi_cache.dto;


public record UserProfileResponse (
        String name,
        String email,
        String city,
        String education,
        String jobTitle
){
}