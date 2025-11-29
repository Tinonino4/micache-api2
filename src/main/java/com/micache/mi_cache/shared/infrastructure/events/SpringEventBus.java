package com.micache.mi_cache.shared.infrastructure.events;

import com.micache.mi_cache.shared.domain.DomainEvent;
import com.micache.mi_cache.shared.domain.EventBus;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventBus implements EventBus {

    private final ApplicationEventPublisher publisher;

    public SpringEventBus(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
