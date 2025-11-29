package com.micache.mi_cache.shared.domain;

public interface EventBus {
    void publish(DomainEvent event);
}