package com.micache.mi_cache.auth.domain.events;

import com.micache.mi_cache.shared.domain.DomainEvent;

public record UserRegisteredEvent(String email, String name, String confirmationLink) implements DomainEvent {
}
