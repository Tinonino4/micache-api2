package com.micache.mi_cache.shared.infrastructure.config;

import com.micache.mi_cache.shared.domain.EventBus;
import com.micache.mi_cache.shared.infrastructure.events.SpringEventBus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    public EventBus eventBus(ApplicationEventPublisher publisher) {
        return new SpringEventBus(publisher);
    }
}
