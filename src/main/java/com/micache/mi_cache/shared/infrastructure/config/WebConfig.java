package com.micache.mi_cache.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        /* .allowedOrigins(
                                "http://localhost:8081",             // Expo Web
                                "http://localhost:19006",        // Expo Dev Tools Web
                                "http://192.168.1.139:8081",      // IP local Web
                                "http://192.168.1.139:8080"       // API llamada desde móvil físico
                        ) */
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
