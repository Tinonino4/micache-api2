package com.micache.mi_cache.auth.domain.events;

import com.micache.mi_cache.shared.domain.DomainEvent;

public record UserRegistrationCompletedEvent(Long userId, String email, String name) implements DomainEvent {
}
